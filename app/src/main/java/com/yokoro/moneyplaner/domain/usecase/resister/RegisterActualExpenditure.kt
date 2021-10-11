package com.yokoro.moneyplaner.domain.usecase.resister

import com.yokoro.moneyplaner.domain.entity.Date
import com.yokoro.moneyplaner.domain.entity.Expenditure
import com.yokoro.moneyplaner.domain.repository.ActualExpenditureRepository
import com.yokoro.moneyplaner.domain.usecase.Either
import com.yokoro.moneyplaner.domain.usecase.IUseCase

class RegisterActualExpenditure (
    val repository: ActualExpenditureRepository
): IUseCase<Expenditure, Expenditure> {

    override fun execute(value: Expenditure): Either<Expenditure> {
        return repository.registerActualExpenditure(value)
    }
}