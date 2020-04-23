/**
 * FileName: Test
 * Author:   star
 * Date:     2019/10/22 16:40
 * Description: 测试JAVA连接以太坊和调用合约函数
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.blockchain;
import Util.Util;
import BCPA.roles.BCPA_PKG;
import BCPA.wrappers.Challenge;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈测试JAVA连接以太坊和调用合约函数〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class EthCaller {
    private Web3j web3j;
    private Credentials credentials;        //账户私钥信息
    private static final String account1_private_key = "4e1b6ab1b2a5f477c416eb275ac5aac4e1257002782f0c3e74f3e8747db30f05";
    private static final String account2_address = "0x0A35173eB82c9D278F0FEdf234C826a72CE59974";
    private static final String account3_address = "0x382c54005BD10062cd1aE50e9f7e2cFfcc38DD0e";
    private String CONTRACT_ADDRESS = "0x187c81F78Faf7EBE68AF673ED2ab35b640dc6bA9";   //部署的AuditorRandao合约地址
    private static AuditorRandao_sol_AuditorRandao contract;        //合约类，由web3j-cli命令行工具在git下生成
    private BigInteger ethBase = BigInteger.valueOf(10).pow(18);    //把以太转化为wei
    private final int INVALID_ID = -1;                              //初始化的ID

    private Map<Integer, List<Integer>> taskCampaignIDs = new HashMap<Integer, List<Integer>>();    //保存taskID->campaigns的映射
    private int n = 10;      //文件总块数
    private int taskID = INVALID_ID;    //生成的taskID
    private int test_bnum = 24;
    private int test_deposit = 10;
    private int test_commitBalkline = 12;
    private int test_commitDeadline = 6;

    //初始化web3j，然后加载合约
    public EthCaller(){
        try {
            web3j = Web3j.build(new HttpService("http://localhost:7545"));      //本地的ganache gui
            credentials = Credentials.create(account1_private_key);  //直接填写第一个账户的私钥
            loadContract();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //加载合约
    private void loadContract(){
        System.out.println("Going to load smart contract AuditorRandao");
        try {
            contract = AuditorRandao_sol_AuditorRandao.load(
                    CONTRACT_ADDRESS, web3j, credentials,
                    new BigInteger("1"), new BigInteger("3000000"));
            System.out.println("Load smart contract done!");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //获得最新的块数，大整数类型
    public BigInteger getLatestBlockNumber() {
        EthBlockNumber result = new EthBlockNumber();
        try {
            result = this.web3j.ethBlockNumber()
                    .sendAsync()
                    .get();
            System.out.println(result.getBlockNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getBlockNumber();
    }

    //获取最新区块数
//    public boolean mineBlock() {
//        System.out.println("mine block!");
//        boolean result = false;
//        try {
//            //EthMining ETH = this.web3j.ethMining();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    //创建审计任务
    public int newTask(String _fileName, int _n, int _numChallenges, int _deposit){
        this.n = _n;    //保存文件总块数
        BigInteger n = BigInteger.valueOf(_n);
        BigInteger numChallenges = BigInteger.valueOf(_numChallenges);
        BigInteger deposit = BigInteger.valueOf(_deposit);   //单位为eth
        BigInteger weiValue = BigInteger.valueOf(_deposit).multiply(ethBase);  //eth转为wei
        System.out.println("Call the method newTask()");
        try{
            //调用合约创建审计任务，weiValue: 转给这个payable函数的金额
            TransactionReceipt receipt = contract.newTask(_fileName, n,  numChallenges, deposit, weiValue).send();
            System.out.println( "newTask TxHash : " + receipt.getTransactionHash());

            //从emit的事件中获取taskID
            for (AuditorRandao_sol_AuditorRandao.LogTaskAddedEventResponse event : contract.getLogTaskAddedEvents(receipt)) {
                System.out.println("LogTaskAdded event fired : " + event.taskID + ", " + event.fileName + "," + event.n + "," + event.numChallenges + "," + event.deposit);
                taskID = event.taskID.intValue();
            }

            //更新task->campaigns的map
//            taskCampaignIDs.put(taskID, new ArrayList<Integer>(_numChallenges));
//            int campaignID = INVALID_ID;
//            for(int i = 0; i < _numChallenges; i ++) {
//                //用测试的参数生成活动
//                campaignID = newCampaign(test_bnum, test_deposit, test_commitBalkline,test_commitDeadline);
//                if ( campaignID != INVALID_ID) {
//                    addCampaignToTask(taskID, campaignID);  //把campaign加入到task中
//                    taskCampaignIDs.get(taskID).add(campaignID);  //加到list中
//                } else
//                    System.out.println("newCampaign when newTask failed!\n");
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  taskID;
    }

    //活动添加到task
    private boolean addCampaignToTask(int _taskID, int _campaignID) {
        BigInteger taskID = BigInteger.valueOf(_taskID);
        BigInteger campaignID = BigInteger.valueOf(_campaignID);
        System.out.println("Call the method addCampaignToTask()");
        try{
            contract.addCampaignToTask(taskID, campaignID).send();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //创建活动
    public int newCampaign(int _bnum, int _deposit, int _commitBalkline, int _commitDeadline){
        BigInteger bnum = BigInteger.valueOf(_bnum);
        BigInteger deposit = BigInteger.valueOf(_deposit);
        BigInteger commitBalkline = BigInteger.valueOf(_commitBalkline);
        BigInteger commitDeadline = BigInteger.valueOf(_commitDeadline);
        BigInteger weiValue = BigInteger.valueOf(_deposit).multiply(ethBase);
        System.out.println("Call the method newCampaign()");
        int campaignID = INVALID_ID;
        try{
            TransactionReceipt receipt = contract.newCampaign(bnum, deposit, commitBalkline, commitDeadline, weiValue).send();
            System.out.println( "newCampaign TxHash : " + receipt.getTransactionHash());

            for (AuditorRandao_sol_AuditorRandao.LogCampaignAddedEventResponse event : contract.getLogCampaignAddedEvents(receipt)) {
                System.out.println("LogCampaignAdded event fired : " + event.campaignID );
                campaignID = event.campaignID.intValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return campaignID;
    }

    //提交随机数的hash
    public void commit(String from, int _campaignID, int _deposit, byte[] _hash){
        BigInteger weiValue = BigInteger.valueOf(_deposit).multiply(ethBase);
        System.out.println("Call the method commit()");

        List inputParams = Arrays.<Type>asList(new Uint256(_campaignID), new DynamicBytes(_hash));
        Function function = new Function("commit", inputParams, Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = BigInteger.valueOf(1);
            BigInteger gasLimit = BigInteger.valueOf(300000);
            Transaction transaction = Transaction.createContractTransaction(from, nonce, gasPrice, gasLimit, weiValue, encodedFunction);
            EthSendTransaction transactionResponse = web3j.ethSendTransaction(transaction).sendAsync().get();

            String txHash = transactionResponse.getTransactionHash();
            System.out.println( "commit TxHash : " + txHash);

            EthGetTransactionReceipt transactionReceipt =  web3j.ethGetTransactionReceipt(txHash).send();

            if (transactionReceipt.getTransactionReceipt().isPresent()) {
                for (AuditorRandao_sol_AuditorRandao.LogCommitEventResponse event : contract.getLogCommitEvents(transactionReceipt.getResult())) {
                    System.out.println("LogCommitEvent event fired : " + event.from + ", " + event.CampaignId + ", " + event.commitment );
                }
            } else {
                // try again until it's mined
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //揭示自己的随机数
    public void commit(int _campaignID, byte[] _hash, int _deposit){
        BigInteger campaignID = BigInteger.valueOf(_campaignID);
        BigInteger weiValue = BigInteger.valueOf(_deposit).multiply(ethBase);

        System.out.println("Call the method commit()");
        try{
            TransactionReceipt receipt = contract.commit(campaignID, _hash, weiValue).send();
            System.out.println( "commit TxHash : " + receipt.getTransactionHash());

            for (AuditorRandao_sol_AuditorRandao.LogCommitEventResponse event : contract.getLogCommitEvents(receipt)) {
                System.out.println("LogCommit event fired : " + event.from + ", " + event.CampaignId + ", " + event.commitment );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //揭示自己的随机数
    public void reveal(int _campaignID, int _s){
        BigInteger campaignID = BigInteger.valueOf(_campaignID);
        BigInteger s = BigInteger.valueOf(_s);
        System.out.println("Call the method reveal()");
        try{
            TransactionReceipt receipt = contract.reveal(campaignID, s).send();
            System.out.println( "reveal TxHash : " + receipt.getTransactionHash());

            for (AuditorRandao_sol_AuditorRandao.LogRevealEventResponse event : contract.getLogRevealEvents(receipt)) {
                System.out.println("LogReveal event fired : " + event.from + ", " + event.CampaignId + ", " + event.secret );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取随机数，设置活动标志
    public void getRandom(int _campaignID){
        BigInteger campaignID = BigInteger.valueOf(_campaignID);
        System.out.println("Call the method getRandom()");
        try{
            TransactionReceipt receipt = contract.getRandom(campaignID).send();
            System.out.println( "getRandom TxHash : " + receipt.getTransactionHash());
        }catch (Exception e){
            //e.printStackTrace();
            System.out.println( "getRandom failed!");
        }
    }



    //获取区块链的一组随机数
    public Challenge getChallenge(int _taskID) {
        int campaignsLen = taskCampaignIDs.get(_taskID).size();
        Challenge challenge = new Challenge(campaignsLen);
        Random random = new Random();           //java自带的随机数
        List<Integer> list = new ArrayList<Integer>(n);
        for(int i = 0; i < n; i ++) {
            list.add(i);
        }

        int index;
        for (int i = 0; i < campaignsLen; i++) {
            index = random.nextInt(list.size());        //从1至 n 中随机选择 L 个不重复的随机数作为 j
            challenge.addToListJ(list.get(index));
            list.remove(index);
            challenge.addToListVj(Util.hashFromStringToZp(getChallengeMsg(taskCampaignIDs.get(_taskID).get(i))+""));      //从 Zp 中随机选择 L 个元素作为vj
        }
        return challenge;
    }

    //获取一个活动的随机数
    public BigInteger getChallengeMsg( int _campaignID){
        BigInteger _taskID = BigInteger.valueOf(taskID);
        BigInteger campaignID = BigInteger.valueOf(_campaignID);
        System.out.println("Call the method getChallengeMsg()");
        try{
            Tuple2<BigInteger, BigInteger> result = contract.getChallengeMsg(_taskID, campaignID).send();
            System.out.println( "getChallengeMsg result : " + result.component1() + "," + result.component2());
            return result.component2();   //暂时只用到vj，没有管j
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




    //活动全部完成，组装生成 challenge message
    public boolean generateChallenge() {
        BigInteger _taskID = BigInteger.valueOf(taskID);
        System.out.println("Call the method generateChallenge()");
        try{
            TransactionReceipt receipt = contract.generateChallenge(_taskID).send();
            System.out.println( "generateChallenge TxHash : " + receipt.getTransactionHash());
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //call 合约方法
    public byte[] getShaCommit(int _secret){
        BigInteger secret = BigInteger.valueOf(_secret);
        System.out.println("Call the method shaCommit()");
        try{
            byte[] result = contract.shaCommit(secret).send();
            String res = Util.hexBytesToString(result);
            System.out.println( "shaCommit(" + secret + ") = " + res);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        EthCaller ethCaller = new EthCaller();      //当前块数4
        BCPA_PKG pkg = new BCPA_PKG();
//        ethCaller.getLatestBlockNumber();
//        ethCaller.newTask("text.txt", 10, 2, 10);   //当前块数9
//        ethCaller.getLatestBlockNumber();

//        ethCaller.getRandom(0);   //块数10
//        ethCaller.getRandom(0);   //块数11
//        ethCaller.getRandom(0);   //块数12
//        ethCaller.getLatestBlockNumber();

//        try {
//            System.out.println("Now waiting for campaign to finish!");
//            TimeUnit.MINUTES.sleep(5);
//            System.out.println("Campaign finished!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        //到达12块，可以commit
//        int secret1 = 111;
//        byte[] campaign0_hash = ethCaller.getShaCommit(secret1);
//        ethCaller.commit(0,  campaign0_hash, 10);   //块数13
//        ethCaller.getLatestBlockNumber();
//
//        int secret2 = 222;
//        byte[] campaign1_hash = ethCaller.getShaCommit(secret2);
//        ethCaller.commit(1,  campaign1_hash, 10);  //块数14
//        ethCaller.getLatestBlockNumber();

//        //mine到块数19开始可以reveal
//        ethCaller.getRandom(0);   //块数15
//        ethCaller.getRandom(0);   //块数16
//        ethCaller.getRandom(0);   //块数17
//        ethCaller.getRandom(0);   //块数18
//        ethCaller.getRandom(0);   //块数19
//        ethCaller.getLatestBlockNumber();
//
//        //进行reveal
//        ethCaller.reveal(0, secret1);   //块数20
//        ethCaller.reveal(1, secret2);   //块数21
//
//        //等到23块开始可以getRandom
//        ethCaller.getRandom(0);   //块数22
//        ethCaller.getRandom(0);   //块数23
//        ethCaller.getLatestBlockNumber();
//        ethCaller.getRandom(0);   //块数24
//        ethCaller.getRandom(1);   //块数25
//        ethCaller.getLatestBlockNumber();

        //生成challenge message
//        ethCaller.generateChallenge();      //块数26
//        ethCaller.getLatestBlockNumber();

        ethCaller.taskID = 0;
        ethCaller.n = 10;
        ethCaller.taskCampaignIDs.put(0, new ArrayList<>());
        ethCaller.taskCampaignIDs.get(0).add(0);
        ethCaller.taskCampaignIDs.get(0).add(1);
        //获得challenge message
        Challenge challenge = ethCaller.getChallenge(0);
        for (int i = 0; i < challenge.getChallengeLen(); i ++) {
            System.out.println(challenge.getVj(i));
        }

    }
}
