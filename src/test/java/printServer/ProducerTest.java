package printServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.current.Baselabel;
import com.bondex.rabbitmq.Producer;
import com.bondex.util.GsonUtil;

public class ProducerTest {

	 /**
     * IO密集型任务  = 一般为2*CPU核心数（常出现于线程中：数据库数据交互、文件上传下载、网络数据传输等等）
     * CPU密集型任务 = 一般为CPU核心数+1（常出现于线程中：复杂算法）
     * 混合型任务  = 视机器配置和复杂度自测而定
     */
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,
     *                           TimeUnit unit,BlockingQueue<Runnable> workQueue)
     * corePoolSize用于指定核心线程数量
     * maximumPoolSize指定最大线程数
     * keepAliveTime和TimeUnit指定线程空闲后的最大存活时间
     * workQueue则是线程池的缓冲队列,还未执行的线程会在队列中等待
     * 监控队列长度，确保队列有界
     * 不当的线程池大小会使得处理速度变慢，稳定性下降，并且导致内存泄露。如果配置的线程过少，则队列会持续变大，消耗过多内存。
     * 而过多的线程又会 由于频繁的上下文切换导致整个系统的速度变缓——殊途而同归。队列的长度至关重要，它必须得是有界的，这样如果线程池不堪重负了它可以暂时拒绝掉新的请求。
     * ExecutorService 默认的实现是一个无界的 LinkedBlockingQueue。
     * 
     * 线程池的工作顺序: corePoolSize -> 任务队列 -> maximumPoolSize -> 拒绝策略
     * 
     */
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(60, 60+1, 10l, TimeUnit.SECONDS,  new LinkedBlockingQueue<Runnable>(1000));

	
		public static void main(String[] args) throws IOException {
			 System.out.println("主线程-"+Thread.currentThread().getName());
			Producer producer = new Producer();
			BlockingQueue<Future<Object>> queue = new LinkedBlockingQueue<Future<Object>>(100);
			
			for (int i = 0; i < 1; i++) {
				
				CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
					Object object =null;
					try {
						System.out.println("执行运行线程-"+Thread.currentThread().getName());
						List<BaseLabelDetail> baselabel = getBaselabel();
						System.out.println(GsonUtil.GsonString(baselabel));
						object = producer.send(baselabel);
						System.out.println(Thread.currentThread().getName()+"获取返回结果："+object);
						return object;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return object;
					
				},executor);
				
				queue.add(future);
			}
					
			   System.out.println("线程等待所有子线程执行完成-"+Thread.currentThread().getName());
			 
			    System.out.println("全部执行完毕-主线程继续执行-"+Thread.currentThread().getName());
		        int size = queue.size();
		        System.out.println("任务队列长度：="+size);
		        for (int i = 0; i < size; i++) {
					try {
						System.out.println("获取最终结果="+queue.take().get(100L, TimeUnit.SECONDS));
					} catch (Exception e) {
						System.err.println("获取返回结果超时："+e.getMessage());
						e.printStackTrace();
					}
				}
			
			while(true){
				
			}
			
			
		}
			public static List<BaseLabelDetail> getBaselabel(){
				
				List<BaseLabelDetail> list = new ArrayList<BaseLabelDetail>();
				BaseLabelDetail baselabel = new BaseLabelDetail();
				baselabel.setCode("cd_label");// 标签业务code
				baselabel.setCodeName("成都标签");
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("template", "shuangliu");
				baselabel.setExtendData(map);
				baselabel.setShowNum("mawb20232649999"); //展示单号
				//baselabel.setTemplateId("2785d11a-e261-4c61-8096-8f9f21e2a3f0"); //重庆标签
				baselabel.setDoctypeId("OrderManageClient"); //系统业务id
				baselabel.setDoctypeName("成都双流业务货运请求"); //业务说明
				baselabel.setCopies(5); //打印份数
				baselabel.setOpid("280602"); //数据创建人
				baselabel.setOpidName("成都资讯/钱力"); //姓名
				//打印内容json
				baselabel.setJsonData("[{\"Mblno\":\"074-28742501\",\"Hblno\":\"77732686\",\"Tquantity\":\"1\",\"Dportcode\":\"CTU\",\"Aprotcode\":\"MEX\"}]");
				list.add(baselabel);
				return list;
			}
			
			
}
		
		
