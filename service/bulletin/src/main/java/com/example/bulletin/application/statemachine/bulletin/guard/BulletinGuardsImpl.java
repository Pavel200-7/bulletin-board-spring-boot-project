package com.example.bulletin.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinExtendedState;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinValidationContext;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.validationdto.BulletinApproveValidationDto;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulletinGuardsImpl implements BulletinGuards {

    private final Validator validator;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final TradeAccountRepository tradeAccountRepository;

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfCanBeApprovedGuard() {
        return context -> {
            log.info("Начался guard checkIfCanBeApprovedGuard.");

            BulletinValidationContext validationContext = new BulletinValidationContext(context);
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);

            if (bulletin == null) {
                log.info("Bulletin not found.");
                validationContext.addObjectError("Bulletin not found");
                return validationContext.reject();
            }

            BulletinApproveValidationDto validationDto = BulletinApproveValidationDto.fromBulletin(bulletin);
            Errors validationErrors = new BeanPropertyBindingResult(validationDto, Bulletin.class.getName());
            validator.validate(validationDto, validationErrors);

            if (validationErrors.hasErrors()) {
                log.info("Bulletin is invalid.");
                validationContext.addErrors(validationErrors);
                return validationContext.reject();
            }

            log.info("Прошел guard checkIfCanBeApprovedGuard.");
            return validationContext.accept();
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserCanBeABulletinPublisherGuard() {
        return context -> {
            log.info("Начался guard checkIfUserCanBeABulletinPublisherGuard.");

            BulletinValidationContext validationContext = new BulletinValidationContext(context);
            UUID userId = securityService.getCurrentUserIdAsUUID();
            Optional<User> user = userRepository.findById(userId);

            if (!user.isPresent()) {
                validationContext.addObjectError("User with this id is not found.");
                return validationContext.reject();
            }
            if (user.get().isBlocked()) {
                validationContext.addObjectError("User with this id is blocked.");
                return validationContext.reject();
            }

            Optional<TradeAccount> tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(userId);
            if (!tradeAccount.isPresent()) {
                validationContext.addObjectError("User with this id does not have trade account.");
                return validationContext.reject();
            }

            if (!tradeAccount.get().isApproved()) {
                validationContext.addObjectError("Trade account of this user is not approved.");
                return validationContext.reject();
            }

            log.info("Прошел guard checkIfUserCanBeABulletinPublisherGuard.");
            return validationContext.accept();
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserIsOwnerGuard() {
        return context -> {
            log.info("Начался guard checkIfUserIsOwnerGuard.");

            BulletinValidationContext validationContext = new BulletinValidationContext(context);
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinExtendedState.BULLETIN, Bulletin.class);

            if (bulletin == null) {
                validationContext.addObjectError("Bulletin not found");
                return validationContext.reject();
            }

            UUID currentUserId = securityService.getCurrentUserIdAsUUID();
            if (!bulletin.getOwnerInfo().isOwnedByUserId(currentUserId)) {
                validationContext.addObjectError("You are not the owner of this bulletin");
                return validationContext.reject();
            }

            log.info("Прошел guard checkIfUserIsOwnerGuard.");
            return validationContext.accept();
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserIsAdminGuard() {
        return context -> {
            BulletinValidationContext validationContext = new BulletinValidationContext(context);
            boolean isAdmin = securityService.isAdmin();

            if (!isAdmin) {
                validationContext.addObjectError("Only administrators can perform this action");
                return validationContext.reject();
            }

            return validationContext.accept();
        };
    }

}
