pragma solidity ^0.5.0;

// version 2.0
//1. 接收用户审计请求
//2. 生成challenge message，本质是一组随机数
//3. 随机选出一个具有审计能力的审计者？ 如何判断是否具有审计能力
//4. 把challenge message发送给CSP
//5. 接收从CSP发来的Proof

contract AuditorRandao {
    modifier onlyFounder() {
      if (msg.sender == founder) _;
    }

    function getFounderAddress() view public returns (address) {
      return founder;
    }
    

    struct Challenge {          //从每轮活动的random中生成
        uint j;                 //j = random % n ; n - 文件分块数
        uint vj;                //vj = random % 160 ; vj ∈ Zp , p = 160
    } 

    struct Proof {              //服务器生成的Proof
        uint miu;               // μ ∈ Zp, p = 160
        string y;               // y = xPu,1 ∈ G1,
        string S;
        string T;
    } 

    struct Task {
        uint256 taskId;             //自动增加，从0开始
        string  fileName;           //审计的文件名
        uint256 n;                  //文件分块数，测试设置为10
        uint256 numChallenges;      //测试的挑战块数，测试设置为2

        //string  userName;         //用户身份
        address auditor;            //本次审计任务的auditor，先设置为就是founder
        //uint32  freq;             //审计频率，以小时为单位，先不管定时的情况，User发送task之后立刻审计

        uint256[] campaigns;        //完成这个task需要的活动数，存储活动的id
        Challenge[]  challenges;    //一组随机数
        Proof   proof;              //CSP生成的proof
        uint96  deposit;            //生成每个challenge需要的费用，测试，10以太币
        uint256 bountypot;          //奖金池，用于奖励auditor
        bool    result;             //审计结果，true表示数据完整；false表示数据不完整
    }

    uint256 public numTasks;        
    Task[] tasks;

    
    //新建一个审计任务
    event LogTaskAdded(uint256 indexed taskID, 
                        string fileName,        
                        uint256 n,              //文件分块数
                        uint256 numChallenges,  //挑战个数
                        uint96 deposit);        //每个挑战的费用
    

    //用户发送请求给founder，由founder来执行创建task   
    function newTask( string calldata _fileName, uint256 _n, uint256 _numChallenges, uint96 _deposit) 
            payable moreThanZero(_deposit)  external returns (uint256 _taskID) {
        _taskID = tasks.length ++;              //taskid 从0开始递增
        Task storage task = tasks[_taskID];     //新加一个task
        numTasks ++;

        task.taskId = _taskID;
        task.fileName = _fileName;
        task.n = _n;
        task.numChallenges = _numChallenges;

        //如何founder作为consumer发起多次campaign？
        // for (uint256 i = 0; i < _numChallenges; i ++) {
        //     //测试，先全部都是25块结束活动
        //     task.campaigns[i] = newCampaign(25, msg.value / numChallenges, 12, 6, 
        //                                  0, false, msg.value / numChallenges)
        // } 

        task.deposit = _deposit;
        task.bountypot = msg.value;

        emit LogTaskAdded(_taskID, _fileName, _n, _numChallenges, _deposit);
    }
 
    //添加活动给审计任务前进行检查
    modifier checkCampaignIDAlreadyExist(uint256 _taskID, uint256 _campaignID) {
        if (_taskID >= numTasks) revert();              //审计任务不存在
        if (_campaignID >= numCampaigns) revert();      //活动不存在
        for (uint i = 0; i < tasks[_taskID].campaigns.length; i ++) {
                if (tasks[_taskID].campaigns[i] == _campaignID)   //已经添加的活动不能再加
                    revert();
        } 
        _;
    }

    event LogAddCampaignToTask(uint256 indexed taskID, uint256 indexed campaignID);

    //为task添加活动，每次newCampaign之后再addCampaignToTask
    function addCampaignToTask(uint256 _taskID, uint256 _campaignID) checkCampaignIDAlreadyExist(_taskID, _campaignID) public { 
        Task storage task= tasks[_taskID]; 
        uint256 _index = task.campaigns.length ++;
        task.campaigns[_index] = _campaignID;
        emit LogAddCampaignToTask(_taskID, _campaignID); 
    }

  

    //检查任务的所有活动是否结束
    modifier checkTaskCampaignAlreadyDone(uint256 _taskID) {
        if (_taskID >= numTasks) revert();         //审计任务不存在
        if (tasks[_taskID].campaigns.length != tasks[_taskID].numChallenges) revert();     //活动数量不等于挑战数目
        for(uint i = 0; i < tasks[_taskID].numChallenges; i ++) {
            if (campaigns[tasks[_taskID].campaigns[i]].settled == false)   //有活动还没生成完随机数
                revert();
        }
        _;
    }


    event LogJandVJ(uint256 indexed campaignID, uint256 random, uint256 j, uint256 vj); 
    event LogGenerateChallenge(uint256 indexed taskID, uint256 numChallenges);  

    //活动全部完成之后，生成challenge随机数组
    function generateChallenge(uint256 _taskID) checkTaskCampaignAlreadyDone(_taskID) public {
         
        Task storage task = tasks[_taskID];
        uint i;  
        for (i = 0; i < task.campaigns.length; i ++) {
            uint256 _campaignID = task.campaigns[i];                //获取活动id  
            uint256 j = campaigns[_campaignID].random % task.n;     //选择第几块文件
            uint256 vj = campaigns[_campaignID].random ;            //需要映射到Zp中   

            //对于重复的 j 进行处理
            // for (uint k = 0; k < task.challenges.length; k ++) {
            //     if (j == task.challenges[k].j) 
            //         j = j + 1;
            // }
            
            task.challenges.push(Challenge(j, vj));  
            
            emit LogJandVJ(_campaignID, campaigns[_campaignID].random, j, vj); 
        }  
            
        emit LogGenerateChallenge(_taskID, i);   
    }

    //检查任务中某个活动的challenge是否已经生成完成
    modifier challengeMsgAlreadyExist(uint256 _taskID, uint256 _campaignID) {
        if (_taskID >= numTasks) revert();              //审计任务不存在
        if (_campaignID >= numCampaigns) revert();       //活动不存在
        if (tasks[_taskID].challenges.length != tasks[_taskID].numChallenges) revert(); //审计任务的挑战没有完全生成好
        uint i;
        for (i = 0; i < tasks[_taskID].numChallenges; i ++) {
            if(_campaignID == tasks[_taskID].campaigns[i])
                break;
        }
        if(i == tasks[_taskID].numChallenges) revert();     //所查询的活动不属于这个审计任务
        _;
    }

    //返回任务中某个活动的challenge值 (j, vj)
    function getChallengeMsg(uint256 _taskID, uint256 _campaignID) challengeMsgAlreadyExist(_taskID, _campaignID) 
        public view returns (uint256 j, uint256 vj) { 
        return (tasks[_taskID].challenges[_campaignID].j, tasks[_taskID].challenges[_campaignID].vj );
    }

    /*
    struct Proof {              //服务器生成的Proof
        uint miu;               //μ ∈ Zp, p = 160
        string y;
        string S;
        string T;
    } 
     */
    
    //获取CS生成的Proof
    function recvProof(uint256 _taskID, uint256 _miu, string calldata _y, string calldata _S, string calldata _T) 
        checkTaskCampaignAlreadyDone(_taskID) external { 
        tasks[_taskID].proof = Proof(_miu, _y, _S, _T);
    }

    //保存本地验证的结果
    function saveResult(uint256 _taskID, bool _result) 
        checkTaskCampaignAlreadyDone(_taskID) external {
        tasks[_taskID].result = _result;
    }



    //测试：获取任务中活动数，应该与挑战数一致
    function getCampaignsLen(uint256 _taskID) public view returns(uint256 len) {
        len = tasks[_taskID].campaigns.length;
    }
    //测试：查看活动是否结束
    function getCampaignSettled(uint256 _campaignID) public view returns(bool settled) {
        settled = campaigns[_campaignID].settled;
    }

    //测试：查看活动的随机值
    function getCampaignRandom(uint256 _campaignID) public view returns(uint256 random) {
        random = campaigns[_campaignID].random;
    }

    //================  原本的randao合约  ================  
    struct Participant {
        uint256   secret;   		//自己的秘密数字
        bytes32   commitment;
        uint256   reward;
        bool      revealed;
        bool      rewarded;
        //add by zx
        //bool      isAuditor;        //是否具备本地进行双线性对计算的能力
    }

    //一般由用户担任，来提出需求
    struct Consumer {
        address caddr;
        uint256 bountypot;  		//奖金池
    }

    //一轮生成随机数和选举Auditor的活动
    struct Campaign {
        uint32    bnum; 			//随机数生成的目标块数
        uint96    deposit;			//参与者需要提交的押金
        uint16    commitBalkline;	//开始提交到目标块的距离
        uint16    commitDeadline;	//结束提交到目标块的距离

        uint256   random;           //最终的随机数结果，对应了选出的audior，需要mod auditor总数，然后由auditor根据该值和k选择challenge message
        bool      settled;
        uint256   bountypot;        //奖金池
        uint32    commitNum;        //提交的人数，应该是n，对应了n个文件块
        uint32    revealsNum;       //揭露的人数

        mapping (address => Consumer) consumers;
        mapping (address => Participant) participants;
        mapping (bytes32 => bool) commitments;
    }

    uint256 public numCampaigns;
    Campaign[] public campaigns;
    address public founder;

    modifier blankAddress(address n) {if (n != address(0)) revert(); _;}

    modifier moreThanZero(uint256 _deposit) {if (_deposit <= 0) revert(); _;}

    modifier notBeBlank(bytes32 _s) {if (_s == "") revert(); _;}

    modifier beBlank(bytes32 _s) {if (_s != "") revert(); _;}

    modifier beFalse(bool _t) {if (_t) revert(); _;}

    constructor() public {
        founder = msg.sender;
    }

    //新建一轮活动
    event LogCampaignAdded(uint256 indexed campaignID,
                            address indexed from,
                            uint32 indexed bnum,
                            uint96 deposit,
                            uint16 commitBalkline,
                            uint16 commitDeadline, 
                            uint256 bountypot);

    modifier timeLineCheck(uint32 _bnum, uint16 _commitBalkline, uint16 _commitDeadline) {
        if (block.number >= _bnum) revert();	//当前块数 < 目标块数
        if (_commitBalkline <= 0) revert();		//开始提交到目标块的距离 > 0
        if (_commitDeadline <= 0) revert();		//结束提交到目标块的距离 > 0
        if (_commitDeadline >= _commitBalkline) revert();	//结束提交到目标块的距离 < 开始提交到目标块的距离
        if (block.number >= _bnum - _commitBalkline) revert(); //当前块数 < 目标块数 - 结束提交到目标块的距离
        _;
    }

    function newCampaign(
        uint32 _bnum,
        uint96 _deposit,
        uint16 _commitBalkline,
        uint16 _commitDeadline
    ) payable
        timeLineCheck(_bnum, _commitBalkline, _commitDeadline) //检查参数
        moreThanZero(_deposit) external returns (uint256 _campaignID) {
        _campaignID = campaigns.length++;
        Campaign storage c = campaigns[_campaignID];	//新加一个活动
        numCampaigns++;
        c.bnum = _bnum;
        c.deposit = _deposit;
        c.commitBalkline = _commitBalkline;
        c.commitDeadline = _commitDeadline;
        c.bountypot = msg.value;						//调用newCampaign的用户需要提供奖金
        c.consumers[msg.sender] = Consumer(msg.sender, msg.value);		//创建需要随机数的用户
        emit LogCampaignAdded(_campaignID, msg.sender, _bnum, _deposit, _commitBalkline, _commitDeadline, msg.value);
    }

    event LogFollow(uint256 indexed CampaignId, address indexed from, uint256 bountypot);

    //随机数需求方可以选择不创建一轮活动，而是选择跟随某一轮随机数活动作为自己的随机数
    function follow(uint256 _campaignID) //参加一次随机过程
        external payable returns (bool) {
        Campaign storage c = campaigns[_campaignID];
        Consumer storage consumer = c.consumers[msg.sender];
        return followCampaign(_campaignID, c, consumer);
    }

    modifier checkFollowPhase(uint256 _bnum, uint16 _commitDeadline) {
        if (block.number > _bnum - _commitDeadline) revert();
        _;
    }

    function followCampaign(
        uint256 _campaignID,
        Campaign storage c,
        Consumer storage consumer
    ) checkFollowPhase(c.bnum, c.commitDeadline) 
        blankAddress(consumer.caddr) internal returns (bool) {
        c.bountypot += msg.value; //调用follow的人需要加押金，加入到活动的奖池中
        c.consumers[msg.sender] = Consumer(msg.sender, msg.value); //作为本次活动的消费者
        emit LogFollow(_campaignID, msg.sender, msg.value);
        return true;
    }

    event LogCommit(uint256 indexed CampaignId, address indexed from, bytes32 commitment);

    //参与者可以通过提交随机数来参与随机数的生成
    function commit(uint256 _campaignID, bytes32 _hs) notBeBlank(_hs) external payable {
        Campaign storage c = campaigns[_campaignID];
        commitmentCampaign(_campaignID, _hs, c);
    }

    modifier checkDeposit(uint256 _deposit) { if (msg.value != _deposit) revert(); _; }

    modifier checkCommitPhase(uint256 _bnum, uint16 _commitBalkline, uint16 _commitDeadline) {
        if (block.number < _bnum - _commitBalkline) revert();
        if (block.number > _bnum - _commitDeadline) revert();
        _;
    }

    function commitmentCampaign(
        uint256 _campaignID,
        bytes32 _hs,   	//随机数的 sha3 值。
        Campaign storage c
    ) checkDeposit(c.deposit)
        checkCommitPhase(c.bnum, c.commitBalkline, c.commitDeadline)
        beBlank(c.participants[msg.sender].commitment) internal { 
        //提交随机数需要发送押金到合约，不能多于或者少于活动押金，必须刚好等于。提交随机数，必须在提交随机数窗口期提交，否则会失败。
        if (c.commitments[_hs]) {
            revert();
        } else {
            c.participants[msg.sender] = Participant(0, _hs, 0, false, false);
            c.commitNum++;
            c.commitments[_hs] = true;
            emit LogCommit(_campaignID, msg.sender, _hs);
        }
    }

    // For test
    function getCommitment(uint256 _campaignID) external view returns (bytes32) {
        Campaign storage c = campaigns[_campaignID];
        Participant storage p = c.participants[msg.sender];  //获取参与者提交的数字
        return p.commitment;
    }

    function shaCommit(uint256 _s) public pure returns (bytes32) {
        return keccak256(abi.encodePacked(_s));
    }

    event LogReveal(uint256 indexed CampaignId, address indexed from, uint256 secret);

    //_s 随机数  随机数提交者可以披露自己的随机数，合约会验证是否是有效的随机数，如果有效，将计算到最终的随机数结果中
    function reveal(uint256 _campaignID, uint256 _s) external {
        Campaign storage c = campaigns[_campaignID];
        Participant storage p = c.participants[msg.sender];
        revealCampaign(_campaignID, _s, c, p);
    }

    modifier checkRevealPhase(uint256 _bnum, uint16 _commitDeadline) {
        if (block.number <= _bnum - _commitDeadline) revert();
        if (block.number >= _bnum) revert();
        _;
    }

    //验证随机数是否有效
    modifier checkSecret(uint256 _s, bytes32 _commitment) {
        if (keccak256(abi.encodePacked(_s)) != _commitment) revert();
        _;
    }

    function revealCampaign(
        uint256 _campaignID,
        uint256 _s,
        Campaign storage c,
        Participant storage p
    ) checkRevealPhase(c.bnum, c.commitDeadline)
        checkSecret(_s, p.commitment)
        beFalse(p.revealed) internal {
        p.secret = _s;
        p.revealed = true;
        c.revealsNum++;
        c.random ^= p.secret;  //接收参与者披露的数字，全部异或
        emit LogReveal(_campaignID, msg.sender, _s);
    }

    modifier bountyPhase(uint256 _bnum){if (block.number < _bnum) revert(); _;}

    //任何人可以在随机数目标块数之后，获取该轮活动的随机数。
    //只有当所有的随机数提交者提交的随机数全部都收集到，才认为本轮随机数生成有效。
    //对于没有在收集阶段提交随机数的参与者，将罚没其提交的押金，并均分给其他参与者。
    function getRandom(uint256 _campaignID) external returns (uint256) {
        Campaign storage c = campaigns[_campaignID];
        return returnRandom(c);
    }

    function returnRandom(Campaign storage c) internal bountyPhase(c.bnum) returns (uint256) {
        if (c.revealsNum == c.commitNum) {
            c.settled = true;
            return c.random;
        }
    }

    // The commiter get his bounty and deposit, there are three situations
    // 1. Campaign succeeds.Every revealer gets his deposit and the bounty.
    // 2. Someone revels, but some does not,Campaign fails.
    // The revealer can get the deposit and the fines are distributed.
    // 3. Nobody reveals, Campaign fails.Every commiter can get his deposit.
    function getMyBounty(uint256 _campaignID) external {
        Campaign storage c = campaigns[_campaignID];
        Participant storage p = c.participants[msg.sender];
        transferBounty(c, p);
    }

    function transferBounty(
        Campaign storage c,
        Participant storage p
        ) bountyPhase(c.bnum)
        beFalse(p.rewarded) internal {
        if (c.revealsNum > 0) { 	//至少有人披露随机数
            if (p.revealed) {    	//本参与者披露了随机数
                uint256 share = calculateShare(c);
                returnReward(share, c, p);
            }
        // Nobody reveals
        } else {
            returnReward(0, c, p);	//没人成功披露随机数，所有参与者取回自己的押金。需求方也可以取回自己的奖池
        }
    }

    function calculateShare(Campaign storage c) internal view returns (uint256 _share) {
        // Someone does not reveal. Campaign fails.
        if (c.commitNum > c.revealsNum) {
            _share = fines(c) / c.revealsNum; 		//将平分未披露随机数的参与者的押金，并返还押金
        // Campaign succeeds.
        } else {
            _share = c.bountypot / c.revealsNum;	//生成成功，将平分奖励费用，并返还押金
        }
    }

    function returnReward(
        uint256 _share,
        Campaign storage c,
        Participant storage p
    ) internal {
        p.reward = _share;
        p.rewarded = true;
        msg.sender.transfer(_share + c.deposit);  //返回得到的奖励以及原来的押金
    }

    function fines(Campaign storage c) internal view returns (uint256) {
        return (c.commitNum - c.revealsNum) * c.deposit;
    }

    // If the campaign fails, the consumers can get back the bounty.
    function refundBounty(uint256 _campaignID) external {
        Campaign storage c = campaigns[_campaignID];
        returnBounty(c);
    }

    modifier campaignFailed(uint32 _commitNum, uint32 _revealsNum) {
        if (_commitNum == _revealsNum && _commitNum != 0) revert();
        _;
    }

    modifier beConsumer(address _caddr) {
        if (_caddr != msg.sender) revert();
        _;
    }

    //本轮随机数生成失败，且没有任何人成功披露随机数，随机数需求方可以通过`refundBounty`函数，返还其提交的奖励 
    function returnBounty(Campaign storage c)
        internal
        bountyPhase(c.bnum)
        campaignFailed(c.commitNum, c.revealsNum)
        beConsumer(c.consumers[msg.sender].caddr) {
        uint256 bountypot = c.consumers[msg.sender].bountypot;
        c.consumers[msg.sender].bountypot = 0;
        msg.sender.transfer(bountypot);
    }
}
