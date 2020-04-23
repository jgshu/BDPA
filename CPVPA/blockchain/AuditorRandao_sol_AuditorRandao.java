package CPVPA.blockchain;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class AuditorRandao_sol_AuditorRandao extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50600480546001600160a01b03191633179055611f28806100326000396000f3fe6080604052600436106101405760003560e01c806369bcdb7d116100b6578063bda09cd41161006f578063bda09cd414610606578063c14bc9fd1461061b578063cd4b69141461064b578063cfb3a49314610675578063e9489a611461069f578063f2f03877146106c957610140565b806369bcdb7d146103ed5780638a8e5afb1461041757806392e099e1146104415780639348cef7146105685780639ae0bdb914610598578063ba487e62146105c257610140565b80633f03b52a116101085780633f03b52a146102f55780634d853ee51461031f57806358b1f29c146103505780635ffccedb1461037c57806364b13d72146103a657806367f5df7d146103bb57610140565b806308b423c114610145578063141961bc1461018e5780632c0f7b6f1461021a578063334ca50014610241578063384b1393146102c4575b600080fd5b34801561015157600080fd5b506101756004803603604081101561016857600080fd5b50803590602001356106ec565b6040805192835260208301919091528051918290030190f35b34801561019a57600080fd5b506101b8600480360360208110156101b157600080fd5b5035610878565b6040805163ffffffff9a8b1681526001600160601b0390991660208a015261ffff97881689820152959096166060880152608087019390935290151560a086015260c0850152841660e084015292166101008201529051908190036101200190f35b34801561022657600080fd5b5061022f6108f1565b60408051918252519081900360200190f35b61022f6004803603608081101561025757600080fd5b810190602081018135600160201b81111561027157600080fd5b82018360208201111561028357600080fd5b803590602001918460018302840111600160201b831117156102a457600080fd5b9193509150803590602081013590604001356001600160601b03166108f7565b6102e1600480360360208110156102da57600080fd5b5035610a22565b604080519115158252519081900360200190f35b34801561030157600080fd5b506102e16004803603602081101561031857600080fd5b5035610a69565b34801561032b57600080fd5b50610334610a95565b604080516001600160a01b039092168252519081900360200190f35b34801561035c57600080fd5b5061037a6004803603602081101561037357600080fd5b5035610aa4565b005b34801561038857600080fd5b5061022f6004803603602081101561039f57600080fd5b5035610ad0565b3480156103b257600080fd5b50610334610af8565b3480156103c757600080fd5b5061037a600480360360408110156103de57600080fd5b50803590602001351515610b08565b3480156103f957600080fd5b5061022f6004803603602081101561041057600080fd5b5035610c29565b34801561042357600080fd5b5061022f6004803603602081101561043a57600080fd5b5035610c63565b34801561044d57600080fd5b5061037a600480360360a081101561046457600080fd5b813591602081013591810190606081016040820135600160201b81111561048a57600080fd5b82018360208201111561049c57600080fd5b803590602001918460018302840111600160201b831117156104bd57600080fd5b919390929091602081019035600160201b8111156104da57600080fd5b8201836020820111156104ec57600080fd5b803590602001918460018302840111600160201b8311171561050d57600080fd5b919390929091602081019035600160201b81111561052a57600080fd5b82018360208201111561053c57600080fd5b803590602001918460018302840111600160201b8311171561055d57600080fd5b509092509050610c87565b34801561057457600080fd5b5061037a6004803603604081101561058b57600080fd5b5080359060200135610ebd565b3480156105a457600080fd5b5061022f600480360360208110156105bb57600080fd5b5035610f00565b61022f600480360360808110156105d857600080fd5b5063ffffffff813516906001600160601b036020820135169061ffff60408201358116916060013516610f29565b34801561061257600080fd5b5061022f61118c565b34801561062757600080fd5b5061037a6004803603604081101561063e57600080fd5b5080359060200135611192565b34801561065757600080fd5b5061022f6004803603602081101561066e57600080fd5b50356112af565b34801561068157600080fd5b5061037a6004803603602081101561069857600080fd5b50356112df565b3480156106ab57600080fd5b5061037a600480360360208110156106c257600080fd5b503561131f565b61037a600480360360408110156106df57600080fd5b5080359060200135611593565b600080838360005482106106ff57600080fd5b600254811061070d57600080fd5b6001828154811061071a57fe5b90600052602060002090600e0201600301546001838154811061073957fe5b90600052602060002090600e0201600601805490501461075857600080fd5b60005b6001838154811061076857fe5b90600052602060002090600e0201600301548110156107c9576001838154811061078e57fe5b90600052602060002090600e020160050181815481106107aa57fe5b90600052602060002001548214156107c1576107c9565b60010161075b565b600183815481106107d657fe5b90600052602060002090600e0201600301548114156107f457600080fd5b6001878154811061080157fe5b90600052602060002090600e0201600601868154811061081d57fe5b9060005260206000209060020201600001546001888154811061083c57fe5b90600052602060002090600e0201600601878154811061085857fe5b906000526020600020906002020160010154945094505050509250929050565b6003818154811061088557fe5b60009182526020909120600890910201805460018201546002830154600384015460049094015463ffffffff8085169650600160201b8086046001600160601b031696600160801b870461ffff90811697600160901b900416959460ff16939092808216929091041689565b60025481565b6000816001600160601b03166000811161091057600080fd5b600180549061092190828101611bb9565b915060006001838154811061093257fe5b60009182526020822082546001908101909355600e90910201848155915061095d9082018989611be5565b506002810186905560038101859055600b810180546bffffffffffffffffffffffff19166001600160601b03861690811790915534600c830155604080516020810189905290810187905260608101919091526080808252810188905283907ff4bcf8c37dcabdd784fd0532253956a631e0af9caa20aea222b3a535163f28df908a908a908a908a908a908060a08101878780828437600083820152604051601f909101601f19169092018290039850909650505050505050a2505095945050505050565b60008060038381548110610a3257fe5b60009182526020808320338452600560089093020191820190526040909120909150610a5f8483836115c8565b925050505b919050565b600060038281548110610a7857fe5b600091825260209091206002600890920201015460ff1692915050565b6004546001600160a01b031681565b600060038281548110610ab357fe5b90600052602060002090600802019050610acc8161169f565b5050565b600060038281548110610adf57fe5b9060005260206000209060080201600101549050919050565b6004546001600160a01b03165b90565b816000548110610b1757600080fd5b60018181548110610b2457fe5b90600052602060002090600e02016003015460018281548110610b4357fe5b90600052602060002090600e02016005018054905014610b6257600080fd5b60005b60018281548110610b7257fe5b90600052602060002090600e020160030154811015610bf357600360018381548110610b9a57fe5b90600052602060002090600e02016005018281548110610bb657fe5b906000526020600020015481548110610bcb57fe5b600091825260209091206002600890920201015460ff16610beb57600080fd5b600101610b65565b508160018481548110610c0257fe5b60009182526020909120600e90910201600d01805460ff1916911515919091179055505050565b60008060038381548110610c3957fe5b60009182526020808320338452600892909202909101600601905260409020600101549392505050565b60408051602080820193909352815180820384018152908201909152805191012090565b876000548110610c9657600080fd5b60018181548110610ca357fe5b90600052602060002090600e02016003015460018281548110610cc257fe5b90600052602060002090600e02016005018054905014610ce157600080fd5b60005b60018281548110610cf157fe5b90600052602060002090600e020160030154811015610d7257600360018381548110610d1957fe5b90600052602060002090600e02016005018281548110610d3557fe5b906000526020600020015481548110610d4a57fe5b600091825260209091206002600890920201015460ff16610d6a57600080fd5b600101610ce4565b50604051806080016040528089815260200188888080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250505090825250604080516020601f890181900481028201810190925287815291810191908890889081908401838280828437600092019190915250505090825250604080516020601f8701819004810282018101909252858152918101919086908690819084018382808284376000920191909152505050915250600180548b908110610e4057fe5b90600052602060002090600e0201600701600082015181600001556020820151816001019080519060200190610e77929190611c63565b5060408201518051610e93916002840191602090910190611c63565b5060608201518051610eaf916003840191602090910190611c63565b505050505050505050505050565b600060038381548110610ecc57fe5b60009182526020808320338452600660089093020191820190526040909120909150610efa8484848461175e565b50505050565b600060018281548110610f0f57fe5b600091825260209091206005600e90920201015492915050565b60008483838263ffffffff164310610f4057600080fd5b60008261ffff1611610f5157600080fd5b60008161ffff1611610f6257600080fd5b8161ffff168161ffff1610610f7657600080fd5b8161ffff16830363ffffffff164310610f8e57600080fd5b866001600160601b031660008111610fa557600080fd5b6003805490610fb79060018301611cd1565b9450600060038681548110610fc857fe5b90600052602060002090600802019050600260008154809291906001019190505550898160000160006101000a81548163ffffffff021916908363ffffffff160217905550888160000160046101000a8154816001600160601b0302191690836001600160601b03160217905550878160000160106101000a81548161ffff021916908361ffff160217905550868160000160126101000a81548161ffff021916908361ffff1602179055503481600301819055506040518060400160405280336001600160a01b0316815260200134815250816005016000336001600160a01b03166001600160a01b0316815260200190815260200160002060008201518160000160006101000a8154816001600160a01b0302191690836001600160a01b03160217905550602082015181600101559050508963ffffffff16336001600160a01b0316877f1fec0e961ed9a5d582eb83ec0029dc7c1f0cdd44cfae6f49c8e316b8005d15168c8c8c3460405180856001600160601b03166001600160601b031681526020018461ffff1661ffff1681526020018361ffff1661ffff16815260200182815260200194505050505060405180910390a45050505050949350505050565b60005481565b818160005482106111a257600080fd5b60025481106111b057600080fd5b60005b600183815481106111c057fe5b90600052602060002090600e0201600501805490508110156112245781600184815481106111ea57fe5b90600052602060002090600e0201600501828154811061120657fe5b9060005260206000200154141561121c57600080fd5b6001016111b3565b5060006001858154811061123457fe5b90600052602060002090600e02019050600081600501805480919060010161125c9190611cfd565b90508482600501828154811061126e57fe5b6000918252602082200191909155604051869188917f4afc56583874974d34bf0b537e0a7c50b77d121e4e82f9821b672b9acc9eb4999190a3505050505050565b600080600383815481106112bf57fe5b906000526020600020906008020190506112d881611866565b9392505050565b6000600382815481106112ee57fe5b6000918252602080832033845260066008909302019182019052604090912090915061131a82826118b8565b505050565b80600054811061132e57600080fd5b6001818154811061133b57fe5b90600052602060002090600e0201600301546001828154811061135a57fe5b90600052602060002090600e0201600501805490501461137957600080fd5b60005b6001828154811061138957fe5b90600052602060002090600e02016003015481101561140a576003600183815481106113b157fe5b90600052602060002090600e020160050182815481106113cd57fe5b9060005260206000200154815481106113e257fe5b600091825260209091206002600890920201015460ff1661140257600080fd5b60010161137c565b5060006001838154811061141a57fe5b600091825260208220600e9091020191505b600582015481101561155757600082600501828154811061144957fe5b90600052602060002001549050600083600201546003838154811061146a57fe5b9060005260206000209060080201600101548161148357fe5b06905060006003838154811061149557fe5b6000918252602080832060016008909302018201546040805180820190915286815280830182815260068b018054808701825590875293909520905160029093020191825592519101556003805491925084917ff8b2e6214ae4f0a10b032860141026002a529ecd3f8898241217d02ef35cc6c991908390811061151557fe5b906000526020600020906008020160010154848460405180848152602001838152602001828152602001935050505060405180910390a250505060010161142c565b60408051828152905185917fcf338151c0f53f0aa5cec7f216dcce7424cbda6ca2e47354243fd7ddbc732e15919081900360200190a250505050565b808061159e57600080fd5b6000600384815481106115ad57fe5b90600052602060002090600802019050610efa848483611934565b815460009063ffffffff811690600160901b900461ffff168082034311156115ef57600080fd5b83546001600160a01b0316801561160557600080fd5b60038601805434908101909155604080518082018252338082526020808301858152600083815260058d018352859020935184546001600160a01b0319166001600160a01b0390911617845551600193909301929092558251938452915191928a927f23dccbd65d14a6f1eca0cb7a8c085ea070cc1dc36fde87272dcd7b2e0c4748b2929181900390910190a35060019695505050505050565b805463ffffffff16438111156116b457600080fd5b600482015463ffffffff80821691600160201b90041680821480156116de575063ffffffff821615155b156116e857600080fd5b3360008181526005860160205260409020546001600160a01b031690811461170f57600080fd5b336000818152600587016020526040808220600101805490839055905190929183156108fc02918491818181858888f19350505050158015611755573d6000803e3d6000fd5b50505050505050565b815463ffffffff811690600160901b900461ffff16808203431161178157600080fd5b81431061178d57600080fd5b84836001015480826040516020018082815260200191505060405160208183030381529060405280519060200120146117c557600080fd5b600385015460ff1680156117d857600080fd5b87865560038601805460ff1916600190811790915560048801805463ffffffff600160201b808304821685019091160267ffffffff000000001990911617905586549088018054909118905560408051898152905133918b917f9141bfaedbc77aa7b8d9c989cd81909d95bb1677e556e34cfd45e50e0bea29679181900360200190a3505050505050505050565b805460009063ffffffff164381111561187e57600080fd5b6004830154600160201b810463ffffffff908116911614156118b25760028301805460ff1916600190811790915583015491505b50919050565b815463ffffffff16438111156118cd57600080fd5b6003820154610100900460ff1680156118e557600080fd5b6004840154600160201b900463ffffffff161561192857600383015460ff161561192357600061191485611abc565b9050611921818686611b2b565b505b610efa565b610efa60008585611b2b565b8054600160201b90046001600160601b031634811461195257600080fd5b815463ffffffff81169061ffff600160801b8204811691600160901b90041681830343101561198057600080fd5b8061ffff16830343111561199357600080fd5b33600090815260068601602052604090206001015480156119b357600080fd5b600087815260078701602052604090205460ff16156119d157600080fd5b6040805160a081018252600080825260208083018b815283850183815260608501848152608086018581523380875260068f01865288872097518855935160018881019190915592516002880155905160039096018054915115156101000261ff001997151560ff1993841617979097169690961790955560048c01805463ffffffff80821684011663ffffffff199091161790558c845260078c01835292859020805490941690921790925582518a8152925190928b927f918c00c65dd2a8dee4c6985d1d67f04aa8cd2c93e8d427d398a90444c7f7c75e92918290030190a35050505050505050565b600481015460009063ffffffff600160201b8204811691161115611b04576004820154600160201b900463ffffffff16611af583611b83565b81611afc57fe5b049050610a64565b60048201546003830154600160201b90910463ffffffff169081611b2457fe5b0492915050565b6002810183905560038101805461ff001916610100179055815460405133916001600160601b03600160201b90910416850180156108fc02916000818181858888f19350505050158015610efa573d6000803e3d6000fd5b8054600482015463ffffffff600160201b808304821692821692909203166001600160601b039190920481169190910216919050565b81548183558181111561131a57600e0281600e02836000526020600020918201910161131a9190611d21565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611c265782800160ff19823516178555611c53565b82800160010185558215611c53579182015b82811115611c53578235825591602001919060010190611c38565b50611c5f929150611de7565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10611ca457805160ff1916838001178555611c53565b82800160010185558215611c53579182015b82811115611c53578251825591602001919060010190611cb6565b81548183558181111561131a5760080281600802836000526020600020918201910161131a9190611e01565b81548183558181111561131a5760008381526020902061131a918101908301611de7565b610b0591905b80821115611c5f576000808255611d416001830182611e52565b600060028301819055600383018190556004830180546001600160a01b0319169055611d71906005840190611e99565b611d7f600683016000611eb7565b60006007830181815590611d966008850182611e52565b611da4600283016000611e52565b611db2600383016000611e52565b505050600b810180546bffffffffffffffffffffffff191690556000600c820155600d8101805460ff19169055600e01611d27565b610b0591905b80821115611c5f5760008155600101611ded565b610b0591905b80821115611c5f5780546001600160a01b031916815560006001820181905560028201805460ff19169055600382015560048101805467ffffffffffffffff19169055600801611e07565b50805460018160011615610100020316600290046000825580601f10611e785750611e96565b601f016020900490600052602060002090810190611e969190611de7565b50565b5080546000825590600052602060002090810190611e969190611de7565b5080546000825560020290600052602060002090810190611e969190610b0591905b80821115611c5f5760008082556001820155600201611ed956fea265627a7a7231582041df36dc46179be74549937170fefbc10929344322f85657fc8112784a5e4eb964736f6c634300050c0032";

    public static final String FUNC_ADDCAMPAIGNTOTASK = "addCampaignToTask";

    public static final String FUNC_CAMPAIGNS = "campaigns";

    public static final String FUNC_COMMIT = "commit";

    public static final String FUNC_FOLLOW = "follow";

    public static final String FUNC_FOUNDER = "founder";

    public static final String FUNC_GENERATECHALLENGE = "generateChallenge";

    public static final String FUNC_GETCAMPAIGNRANDOM = "getCampaignRandom";

    public static final String FUNC_GETCAMPAIGNSETTLED = "getCampaignSettled";

    public static final String FUNC_GETCAMPAIGNSLEN = "getCampaignsLen";

    public static final String FUNC_GETCHALLENGEMSG = "getChallengeMsg";

    public static final String FUNC_GETCOMMITMENT = "getCommitment";

    public static final String FUNC_GETFOUNDERADDRESS = "getFounderAddress";

    public static final String FUNC_GETMYBOUNTY = "getMyBounty";

    public static final String FUNC_GETRANDOM = "getRandom";

    public static final String FUNC_NEWCAMPAIGN = "newCampaign";

    public static final String FUNC_NEWTASK = "newTask";

    public static final String FUNC_NUMCAMPAIGNS = "numCampaigns";

    public static final String FUNC_NUMTASKS = "numTasks";

    public static final String FUNC_RECVPROOF = "recvProof";

    public static final String FUNC_REFUNDBOUNTY = "refundBounty";

    public static final String FUNC_REVEAL = "reveal";

    public static final String FUNC_SAVERESULT = "saveResult";

    public static final String FUNC_SHACOMMIT = "shaCommit";

    public static final Event LOGADDCAMPAIGNTOTASK_EVENT = new Event("LogAddCampaignToTask", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event LOGCAMPAIGNADDED_EVENT = new Event("LogCampaignAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint32>(true) {}, new TypeReference<Uint96>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGCOMMIT_EVENT = new Event("LogCommit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event LOGFOLLOW_EVENT = new Event("LogFollow", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGGENERATECHALLENGE_EVENT = new Event("LogGenerateChallenge", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGJANDVJ_EVENT = new Event("LogJandVJ", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGREVEAL_EVENT = new Event("LogReveal", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGTASKADDED_EVENT = new Event("LogTaskAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint96>() {}));
    ;

    @Deprecated
    protected AuditorRandao_sol_AuditorRandao(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected AuditorRandao_sol_AuditorRandao(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected AuditorRandao_sol_AuditorRandao(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected AuditorRandao_sol_AuditorRandao(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<LogAddCampaignToTaskEventResponse> getLogAddCampaignToTaskEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGADDCAMPAIGNTOTASK_EVENT, transactionReceipt);
        ArrayList<LogAddCampaignToTaskEventResponse> responses = new ArrayList<LogAddCampaignToTaskEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogAddCampaignToTaskEventResponse typedResponse = new LogAddCampaignToTaskEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogAddCampaignToTaskEventResponse> logAddCampaignToTaskEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogAddCampaignToTaskEventResponse>() {
            @Override
            public LogAddCampaignToTaskEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGADDCAMPAIGNTOTASK_EVENT, log);
                LogAddCampaignToTaskEventResponse typedResponse = new LogAddCampaignToTaskEventResponse();
                typedResponse.log = log;
                typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogAddCampaignToTaskEventResponse> logAddCampaignToTaskEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGADDCAMPAIGNTOTASK_EVENT));
        return logAddCampaignToTaskEventFlowable(filter);
    }

    public List<LogCampaignAddedEventResponse> getLogCampaignAddedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGCAMPAIGNADDED_EVENT, transactionReceipt);
        ArrayList<LogCampaignAddedEventResponse> responses = new ArrayList<LogCampaignAddedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogCampaignAddedEventResponse typedResponse = new LogCampaignAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.bnum = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.deposit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.commitBalkline = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.commitDeadline = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.bountypot = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogCampaignAddedEventResponse> logCampaignAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogCampaignAddedEventResponse>() {
            @Override
            public LogCampaignAddedEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGCAMPAIGNADDED_EVENT, log);
                LogCampaignAddedEventResponse typedResponse = new LogCampaignAddedEventResponse();
                typedResponse.log = log;
                typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.bnum = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.deposit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.commitBalkline = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.commitDeadline = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.bountypot = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogCampaignAddedEventResponse> logCampaignAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGCAMPAIGNADDED_EVENT));
        return logCampaignAddedEventFlowable(filter);
    }

    public List<LogCommitEventResponse> getLogCommitEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGCOMMIT_EVENT, transactionReceipt);
        ArrayList<LogCommitEventResponse> responses = new ArrayList<LogCommitEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogCommitEventResponse typedResponse = new LogCommitEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.commitment = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogCommitEventResponse> logCommitEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogCommitEventResponse>() {
            @Override
            public LogCommitEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGCOMMIT_EVENT, log);
                LogCommitEventResponse typedResponse = new LogCommitEventResponse();
                typedResponse.log = log;
                typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.commitment = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogCommitEventResponse> logCommitEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGCOMMIT_EVENT));
        return logCommitEventFlowable(filter);
    }

    public List<LogFollowEventResponse> getLogFollowEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGFOLLOW_EVENT, transactionReceipt);
        ArrayList<LogFollowEventResponse> responses = new ArrayList<LogFollowEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogFollowEventResponse typedResponse = new LogFollowEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.bountypot = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogFollowEventResponse> logFollowEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogFollowEventResponse>() {
            @Override
            public LogFollowEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGFOLLOW_EVENT, log);
                LogFollowEventResponse typedResponse = new LogFollowEventResponse();
                typedResponse.log = log;
                typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.bountypot = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogFollowEventResponse> logFollowEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGFOLLOW_EVENT));
        return logFollowEventFlowable(filter);
    }

    public List<LogGenerateChallengeEventResponse> getLogGenerateChallengeEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGGENERATECHALLENGE_EVENT, transactionReceipt);
        ArrayList<LogGenerateChallengeEventResponse> responses = new ArrayList<LogGenerateChallengeEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogGenerateChallengeEventResponse typedResponse = new LogGenerateChallengeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.numChallenges = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogGenerateChallengeEventResponse> logGenerateChallengeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogGenerateChallengeEventResponse>() {
            @Override
            public LogGenerateChallengeEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGGENERATECHALLENGE_EVENT, log);
                LogGenerateChallengeEventResponse typedResponse = new LogGenerateChallengeEventResponse();
                typedResponse.log = log;
                typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.numChallenges = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogGenerateChallengeEventResponse> logGenerateChallengeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGGENERATECHALLENGE_EVENT));
        return logGenerateChallengeEventFlowable(filter);
    }

    public List<LogJandVJEventResponse> getLogJandVJEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGJANDVJ_EVENT, transactionReceipt);
        ArrayList<LogJandVJEventResponse> responses = new ArrayList<LogJandVJEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogJandVJEventResponse typedResponse = new LogJandVJEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.random = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.j = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.vj = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogJandVJEventResponse> logJandVJEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogJandVJEventResponse>() {
            @Override
            public LogJandVJEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGJANDVJ_EVENT, log);
                LogJandVJEventResponse typedResponse = new LogJandVJEventResponse();
                typedResponse.log = log;
                typedResponse.campaignID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.random = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.j = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.vj = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogJandVJEventResponse> logJandVJEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGJANDVJ_EVENT));
        return logJandVJEventFlowable(filter);
    }

    public List<LogRevealEventResponse> getLogRevealEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGREVEAL_EVENT, transactionReceipt);
        ArrayList<LogRevealEventResponse> responses = new ArrayList<LogRevealEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogRevealEventResponse typedResponse = new LogRevealEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.secret = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogRevealEventResponse> logRevealEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogRevealEventResponse>() {
            @Override
            public LogRevealEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGREVEAL_EVENT, log);
                LogRevealEventResponse typedResponse = new LogRevealEventResponse();
                typedResponse.log = log;
                typedResponse.CampaignId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.from = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.secret = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogRevealEventResponse> logRevealEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGREVEAL_EVENT));
        return logRevealEventFlowable(filter);
    }

    public List<LogTaskAddedEventResponse> getLogTaskAddedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGTASKADDED_EVENT, transactionReceipt);
        ArrayList<LogTaskAddedEventResponse> responses = new ArrayList<LogTaskAddedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogTaskAddedEventResponse typedResponse = new LogTaskAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.fileName = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.n = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.numChallenges = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.deposit = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogTaskAddedEventResponse> logTaskAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogTaskAddedEventResponse>() {
            @Override
            public LogTaskAddedEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGTASKADDED_EVENT, log);
                LogTaskAddedEventResponse typedResponse = new LogTaskAddedEventResponse();
                typedResponse.log = log;
                typedResponse.taskID = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.fileName = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.n = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.numChallenges = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.deposit = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogTaskAddedEventResponse> logTaskAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGTASKADDED_EVENT));
        return logTaskAddedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addCampaignToTask(BigInteger _taskID, BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDCAMPAIGNTOTASK, 
                Arrays.<Type>asList(new Uint256(_taskID),
                new Uint256(_campaignID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>> campaigns(BigInteger param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CAMPAIGNS, 
                Arrays.<Type>asList(new Uint256(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}, new TypeReference<Uint96>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}, new TypeReference<Uint32>() {}));
        return new RemoteFunctionCall<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (Boolean) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> commit(BigInteger _campaignID, byte[] _hs, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_COMMIT, 
                Arrays.<Type>asList(new Uint256(_campaignID),
                new Bytes32(_hs)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> follow(BigInteger _campaignID, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_FOLLOW, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<String> founder() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FOUNDER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> generateChallenge(BigInteger _taskID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GENERATECHALLENGE, 
                Arrays.<Type>asList(new Uint256(_taskID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getCampaignRandom(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCAMPAIGNRANDOM, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> getCampaignSettled(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCAMPAIGNSETTLED, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getCampaignsLen(BigInteger _taskID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCAMPAIGNSLEN, 
                Arrays.<Type>asList(new Uint256(_taskID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getChallengeMsg(BigInteger _taskID, BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCHALLENGEMSG, 
                Arrays.<Type>asList(new Uint256(_taskID),
                new Uint256(_campaignID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<byte[]> getCommitment(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCOMMITMENT, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> getFounderAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFOUNDERADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> getMyBounty(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETMYBOUNTY, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> getRandom(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETRANDOM, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> newCampaign(BigInteger _bnum, BigInteger _deposit, BigInteger _commitBalkline, BigInteger _commitDeadline, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_NEWCAMPAIGN, 
                Arrays.<Type>asList(new Uint32(_bnum),
                new Uint96(_deposit),
                new Uint16(_commitBalkline),
                new Uint16(_commitDeadline)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> newTask(String _fileName, BigInteger _n, BigInteger _numChallenges, BigInteger _deposit, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_NEWTASK, 
                Arrays.<Type>asList(new Utf8String(_fileName),
                new Uint256(_n),
                new Uint256(_numChallenges),
                new Uint96(_deposit)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> numCampaigns() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NUMCAMPAIGNS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> numTasks() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NUMTASKS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> recvProof(BigInteger _taskID, BigInteger _miu, String _y, String _S, String _T) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RECVPROOF, 
                Arrays.<Type>asList(new Uint256(_taskID),
                new Uint256(_miu),
                new Utf8String(_y),
                new Utf8String(_S),
                new Utf8String(_T)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> refundBounty(BigInteger _campaignID) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REFUNDBOUNTY, 
                Arrays.<Type>asList(new Uint256(_campaignID)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> reveal(BigInteger _campaignID, BigInteger _s) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVEAL, 
                Arrays.<Type>asList(new Uint256(_campaignID),
                new Uint256(_s)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> saveResult(BigInteger _taskID, Boolean _result) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SAVERESULT, 
                Arrays.<Type>asList(new Uint256(_taskID),
                new Bool(_result)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> shaCommit(BigInteger _s) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SHACOMMIT, 
                Arrays.<Type>asList(new Uint256(_s)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    @Deprecated
    public static AuditorRandao_sol_AuditorRandao load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuditorRandao_sol_AuditorRandao(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static AuditorRandao_sol_AuditorRandao load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AuditorRandao_sol_AuditorRandao(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static AuditorRandao_sol_AuditorRandao load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new AuditorRandao_sol_AuditorRandao(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static AuditorRandao_sol_AuditorRandao load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new AuditorRandao_sol_AuditorRandao(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<AuditorRandao_sol_AuditorRandao> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(AuditorRandao_sol_AuditorRandao.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<AuditorRandao_sol_AuditorRandao> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(AuditorRandao_sol_AuditorRandao.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<AuditorRandao_sol_AuditorRandao> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AuditorRandao_sol_AuditorRandao.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<AuditorRandao_sol_AuditorRandao> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AuditorRandao_sol_AuditorRandao.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class LogAddCampaignToTaskEventResponse extends BaseEventResponse {
        public BigInteger taskID;

        public BigInteger campaignID;
    }

    public static class LogCampaignAddedEventResponse extends BaseEventResponse {
        public BigInteger campaignID;

        public String from;

        public BigInteger bnum;

        public BigInteger deposit;

        public BigInteger commitBalkline;

        public BigInteger commitDeadline;

        public BigInteger bountypot;
    }

    public static class LogCommitEventResponse extends BaseEventResponse {
        public BigInteger CampaignId;

        public String from;

        public byte[] commitment;
    }

    public static class LogFollowEventResponse extends BaseEventResponse {
        public BigInteger CampaignId;

        public String from;

        public BigInteger bountypot;
    }

    public static class LogGenerateChallengeEventResponse extends BaseEventResponse {
        public BigInteger taskID;

        public BigInteger numChallenges;
    }

    public static class LogJandVJEventResponse extends BaseEventResponse {
        public BigInteger campaignID;

        public BigInteger random;

        public BigInteger j;

        public BigInteger vj;
    }

    public static class LogRevealEventResponse extends BaseEventResponse {
        public BigInteger CampaignId;

        public String from;

        public BigInteger secret;
    }

    public static class LogTaskAddedEventResponse extends BaseEventResponse {
        public BigInteger taskID;

        public String fileName;

        public BigInteger n;

        public BigInteger numChallenges;

        public BigInteger deposit;
    }
}
