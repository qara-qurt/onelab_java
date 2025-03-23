package org.onelab.camunda_service.delegate;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.onelab.camunda_service.dto.OrderDto;
import org.springframework.stereotype.Component;

@Slf4j
@Component("sendNotificationDelegate")
public class SendNotificationDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        boolean paymentSuccess = (Boolean) execution.getVariable("paymentSuccess");
        Long orderId = (Long) execution.getVariable("orderId");

        Thread.sleep(5000);
        if (paymentSuccess) {
            log.info("Notification: order is paid and ready, order id - {}",orderId);
        } else {
            log.info("Notification: order payment failed, order id - {}", orderId);
        }
    }
}
