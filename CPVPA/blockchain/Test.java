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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈测试JAVA连接以太坊和调用合约函数〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class Test {
    private Web3j web3j;
    private Credentials credentials;    //第一个账户的私钥
    private String CONTRACT_ADDRESS = "0x187c81F78Faf7EBE68AF673ED2ab35b640dc6bA9";   //部署的AuditorRandao合约地址
    private static AuditorRandao_sol_AuditorRandao contract;
    private BigInteger ethBase = BigInteger.valueOf(10).pow(18);   // 1 eth = 10^18 wei

    public Test(){
        try {
            web3j = Web3j.build(new HttpService("http://localhost:7545"));  //本地的ganache gui
            credentials = Credentials.create("4e1b6ab1b2a5f477c416eb275ac5aac4e1257002782f0c3e74f3e8747db30f05");  //直接填写第一个账户的私钥
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //加载已经部署的合约
    public void loadContract(){
        System.out.println("Going to load smart contract");
        try {
            contract = AuditorRandao_sol_AuditorRandao.load(
                    CONTRACT_ADDRESS, web3j, credentials,
                    new BigInteger("22000000000"), new BigInteger("510000"));
            System.out.println("Load smart contract done!");


        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //交易形式调用合约方法
    public TransactionReceipt newTask(int _n, int _numChallenges, int _deposit){
        BigInteger n = BigInteger.valueOf(_n);
        BigInteger numChallenges = BigInteger.valueOf(_numChallenges);
        BigInteger deposit = BigInteger.valueOf(_deposit);
        BigInteger weiValue = BigInteger.valueOf(10).multiply(ethBase);  //10 eth
        System.out.println("Call the method newTask()");
        try{
            TransactionReceipt receipt = contract.newTask("file.txt", n,  numChallenges, deposit, weiValue).send();
            System.out.println( "newTask TxHash : " + receipt.getTransactionHash());
            return  receipt;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //call 合约方法
    public void getShaCommit(BigInteger secret){
        System.out.println("Call the method shaCommit()");
        try{
            byte[] result = contract.shaCommit(secret).send();
            String res = Util.hexBytesToString(result);
            System.out.println( "shaCommit(" + secret + ") = " + res);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //=====================  测试以太坊环境的方法  =====================
    public void GetClientVersion(){
        try {
            Web3ClientVersion version = web3j.web3ClientVersion().sendAsync().get();
            System.out.println("version : " + version.getWeb3ClientVersion());
        }catch(Exception e){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getBlockNumber();
    }

    //获得所有以太坊账户地址，这里是10个测试账户
    public List<String> getEthAccounts() {
        EthAccounts result = new EthAccounts();
        try {
            result = this.web3j.ethAccounts()
                    .sendAsync()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getAccounts();
    }

    //获得交易总数，是大整数类型
    public BigInteger getTransactionCount() {
        EthGetTransactionCount result = new EthGetTransactionCount();
        try {
            result = this.web3j.ethGetTransactionCount(this.CONTRACT_ADDRESS,
                    DefaultBlockParameter.valueOf("latest"))
                    .sendAsync()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getTransactionCount();
    }

    //获得某个账户余额，大整数类型
    public BigInteger getAccountBalance(String contractAddress) {
        EthGetBalance result = new EthGetBalance();
        try {
            this.web3j.ethGetBalance(contractAddress,
                    DefaultBlockParameter.valueOf("latest"))
                    .sendAsync()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.getBalance();  //报错：org.web3j.exceptions.MessageDecodingException: Value must be in format 0x[1-9]+[0-9]* or 0x0
    }



    public static void main(String args[]){
        Test test = new Test();
        // 测试ganache的功能
        test.GetClientVersion();   //查看测试环境版本
        System.out.println("最新块数：" + test.getLatestBlockNumber());    //查看最新块数
        System.out.println("交易总数：" + test.getTransactionCount());     //查看交易总数
        System.out.println("所有账户地址如下：");
        List<String> accs = test.getEthAccounts();          //打印所有账户
        for (int i = 0; i < accs.size(); i ++)
            System.out.println(accs.get(i));
        //System.out.println("第10个账户余额" + test.getAccountBalance(accs.get(9)));   //查看第10个账户余额

        test.loadContract();    //加载已经部署的AuditorRandao合约
        TransactionReceipt transactionReceipt = test.newTask(10,2,10);  //调用合约方法

         // Events enable us to log specific events happening during the execution of our smart
        // contract to the blockchain. Index events cannot be logged in their entirety.taskID
        // For Strings and arrays, the hash of values is provided, not the original value.
        // For further information, refer to https://docs.web3j.io/filters.html#filters-and-events
        //打印事件信息
        for (AuditorRandao_sol_AuditorRandao.LogTaskAddedEventResponse event : contract.getLogTaskAddedEvents(transactionReceipt)) {
            System.out.println("LogTaskAdded event fired : " + event.taskID + ", " + event.fileName + ", " + event.n + ", " + event.numChallenges + ", " + event.deposit);
        }

        //测试call方法，调用成功
        BigInteger secret = BigInteger.valueOf(111);
        test.getShaCommit(secret);

    }
}
