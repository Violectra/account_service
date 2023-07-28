package account.services;

import account.models.dto.PaymentDTO;
import account.models.dto.PaymentResponseDTO;
import account.models.entities.PaymentEntity;
import account.models.entities.UserEntity;
import account.models.exceptions.BadRequestException;
import account.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final UserService userService;

    private static final List<String> MONTHS = List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    @Transactional
    public void paymentAdd(List<PaymentDTO> payments) {
            List<String> emails = payments.stream().map(paymentDTO -> paymentDTO.getEmployee().toLowerCase()).distinct().collect(Collectors.toList());
            List<UserEntity> list = userService.findByEmails(emails);
            if (list.size() < emails.size()) {
                throw new BadRequestException("User not found");
            }
            List<PaymentEntity> en = convert(payments, list);
            paymentRepository.saveAll(en);

    }

    @Transactional
    public void paymentUpdate(PaymentDTO payment) {
        try {
            UserEntity user = userService.findByEmail(payment.getEmployee().toLowerCase());
            Optional<PaymentEntity> byUserPeriod = paymentRepository.findByEmployeeAndPeriod(user, payment.getPeriod());
            if (byUserPeriod.isPresent()) {
                PaymentEntity paymentEntity = byUserPeriod.get();
                paymentEntity.setSalary(payment.getSalary());
                paymentRepository.save(paymentEntity);
            } else {
                throw new BadRequestException("Payment not found");
            }

        } catch (RuntimeException e) {
            throw new BadRequestException("Pser not found");
        }
    }

    public PaymentResponseDTO findByUser(UserEntity userEntity, String period) {
        Optional<PaymentEntity> optionalPayment = paymentRepository.findByEmployeeAndPeriod(userEntity, period);
        if (optionalPayment.isEmpty()) {
            return getPaymentResponseDTO(userEntity, period, 0);
        }
        PaymentEntity payment = optionalPayment.get();
        return getPaymentResponseDTO(userEntity, payment.getPeriod(), payment.getSalary());
    }

    @Transactional
    public List<PaymentResponseDTO> findByUser(UserEntity userEntity) {
        List<PaymentEntity> payments = paymentRepository.findByEmployee(userEntity);
        return payments.stream().sorted((b, a) -> {
                    String[] splitA = a.getPeriod().split("-");
                    String[] splitB = b.getPeriod().split("-");
                    if (splitA[1].equals(splitB[1])) {
                        int monthA = Integer.parseInt(splitA[0]);
                        int monthB = Integer.parseInt(splitB[0]);
                        return Integer.compare(monthA, monthB);
                    }
                    return Integer.compare(Integer.parseInt(splitA[1]), Integer.parseInt(splitB[1]));
                }).map(p -> getPaymentResponseDTO(userEntity, p.getPeriod(), p.getSalary()))
                .collect(Collectors.toList());
    }

    private PaymentResponseDTO getPaymentResponseDTO(UserEntity userEntity, String period, long salary) {
        String[] split = period.split("-");
        String monthPeriod = toMonth(split[0]) + "-" + split[1];
        String monthSalary = toDollars(salary);
        return new PaymentResponseDTO(userEntity.getName(), userEntity.getLastname(), monthPeriod, monthSalary);
    }

    private String toDollars(long salary) {
        long dollars = salary / 100;
        long cents = salary % 100;
        return String.format("%s dollar(s) %s cent(s)", dollars, cents);
    }

    private String toMonth(String s) {
        return MONTHS.get(Integer.parseInt(s) - 1);
    }

    private static List<PaymentEntity> convert(List<PaymentDTO> payments, List<UserEntity> list) {
        Map<String, UserEntity> map = list.stream().collect(Collectors.toMap(UserEntity::getEmail, p -> p));
        return payments.stream()
                .map(p -> new PaymentEntity(map.get(p.getEmployee()), p.getPeriod(), p.getSalary())).toList();
    }
}
