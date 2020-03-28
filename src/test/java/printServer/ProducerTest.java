package printServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bondex.entity.current.Baselabel;
import com.bondex.rabbitmq.Producer;

public class ProducerTest {

		public static void main(String[] args) throws IOException {
			Producer producer = new Producer();
			List<Baselabel> list = new ArrayList<Baselabel>();
			Baselabel baselabel = new Baselabel();
			baselabel.setShowNum("mawb2020022488888"); //展示单号
			baselabel.setDocTypeId("OrderManageClient");
			baselabel.setDocTypeName("订单管理请求");
			baselabel.setCopies(5);
			baselabel.setOpid("231243");
			baselabel.setOpidName("重庆空运操作/邹凤");
			baselabel.setJsonData("[{\"Mblno\":\"074-28742501\",\"Hblno\":\"77732686\",\"Tquantity\":\"1\",\"Dportcode\":\"CTU\",\"Aprotcode\":\"MEX\"}]");
			list.add(baselabel);
			producer.send(list);
			while(true){
				
			}
		}
}
