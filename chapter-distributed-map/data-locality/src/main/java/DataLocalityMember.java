import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Partition;
import com.hazelcast.core.PartitionService;

import java.util.Map;

public class DataLocalityMember {
    public static void main(String[] args) {
        Config config= new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        Map<Long, Customer> customerMap = hz.getMap("customers");
        Map<OrderKey, Order> orderMap = hz.getMap("orders");

        long customerId = 100;
        long orderId = 200;
        long articleId = 300;
        Customer customer = new Customer(customerId);
        customerMap.put(customerId, customer);
        OrderKey orderKey = new OrderKey(orderId, customer.id);
        Order order = new Order(orderKey.orderId, customer.id, articleId);
        orderMap.put(orderKey, order);

        PartitionService pService = hz.getPartitionService();
        Partition cPartition = pService.getPartition(customerId);
        Partition kPartition = pService.getPartition(orderKey);
        Partition oPartition = pService.getPartition(orderId);
        System.out.printf("Partition for customer: %s\n", cPartition.getPartitionId());
        System.out.printf("Partition for order with OrderKey: %s\n", kPartition.getPartitionId());
        System.out.printf("Partition for order without OrderKey: %s\n", oPartition.getPartitionId());
    }
}
