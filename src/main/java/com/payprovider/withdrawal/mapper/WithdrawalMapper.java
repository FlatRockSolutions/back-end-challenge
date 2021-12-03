package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.RequestWithdrawalDto;
import com.payprovider.withdrawal.dto.WithdrawalDto;
import com.payprovider.withdrawal.model.Withdrawal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    @Mappings({
        @Mapping(target = "user.id", source = "userId"),
        @Mapping(target = "paymentMethod.id", source = "paymentMethodId")
    })
    Withdrawal requestWithdrawalDtoToWithdrawal(RequestWithdrawalDto withdrawalDto);

    @Mappings({
        @Mapping(target = "userId", source = "user.id"),
        @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    })
    WithdrawalDto withdrawalToWithdrawalDto(Withdrawal withdrawal);

    List<WithdrawalDto> withdrawalToWithdrawalDtoList(List<Withdrawal> employees);
}
