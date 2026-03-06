package com.example.bulletin.application.statemachine.bulletin.guard;

import com.example.bulletin.application.statemachine.bulletin.contract.BulletinSMHeaderContract;
import com.example.bulletin.application.statemachine.bulletin.guard.helper.BulletinApproveValidationDto;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.TradeAccount;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.TradeAccountRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            List<String> errors = new ArrayList<>();
            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);

            if (bulletin == null) {
                errors.add("Bulletin not found");
                saveErrors(context, errors);
                return false;
            }

            BulletinApproveValidationDto validationDto = BulletinApproveValidationDto.fromBulletin(bulletin);
            Errors validationErrors = new BeanPropertyBindingResult(validationDto, Bulletin.class.getName());
            validator.validate(validationDto, validationErrors);

            if (validationErrors.hasErrors()) {
                validationErrors.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .forEach(errorMes -> errors.add(errorMes));
                saveErrors(context, errors);
                return false;
            }

            saveErrors(context, new ArrayList<>());
            return true;
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserCanBeABulletinPublisherGuard() {
        return context -> {
            List<String> errors = new ArrayList<>();

            UUID userId = securityService.getCurrentUserIdAsUUID();
            Optional<User> user = userRepository.findById(userId);

            if (!user.isPresent()) {
                errors.add("User with this id is not found.");
                saveErrors(context, errors);
                return false;
            }
            if (user.get().isBlocked()) {
                errors.add("User with this id is blocked.");
                saveErrors(context, errors);
                return false;
            }

            Optional<TradeAccount> tradeAccount = tradeAccountRepository.findByOwnerInfo_Owner_Id(userId);
            if (!tradeAccount.isPresent()) {
                errors.add("User with this id does not have trade account.");
                saveErrors(context, errors);
                return false;
            }

            if (!tradeAccount.get().isApproved()) {
                errors.add("Trade account of this user is not approved.");
                saveErrors(context, errors);
                return false;
            }

            saveErrors(context, new ArrayList<>());
            return true;
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserIsOwnerGuard() {
        return context -> {
            List<String> errors = new ArrayList<>();

            Bulletin bulletin = context.getExtendedState()
                    .get(BulletinSMHeaderContract.BULLETIN_HEADER, Bulletin.class);

            if (bulletin == null) {
                errors.add("Bulletin not found");
                saveErrors(context, errors);
                return false;
            }

            UUID currentUserId = securityService.getCurrentUserIdAsUUID();
            if (!bulletin.getOwnerInfo().isOwnedByUserId(currentUserId)) {
                errors.add("You are not the owner of this bulletin");
                saveErrors(context, errors);
                return false;
            }

            saveErrors(context, new ArrayList<>());
            return true;
        };
    }

    @Override
    public Guard<BulletinState, BulletinEvent> checkIfUserIsAdminGuard() {
        return context -> {
            List<String> errors = new ArrayList<>();

            boolean isAdmin = securityService.isAdmin();

            if (!isAdmin) {
                errors.add("Only administrators can perform this action");
                saveErrors(context, errors);
                return false;
            }

            saveErrors(context, new ArrayList<>());
            return true;
        };
    }

    private void saveErrors(StateContext<BulletinState, BulletinEvent> context, List<String> errors) {
        context.getExtendedState().getVariables()
                .put(BulletinSMHeaderContract.BULLETIN_VALIDATION_RESULT_HEADER, errors);
    }

}
