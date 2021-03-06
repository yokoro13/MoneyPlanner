package com.yokoro.moneyplanner.data.datastore.plan

import com.yokoro.moneyplanner.di.AppDatabase
import com.yokoro.moneyplanner.data.database.plan.LocalExpectedIncome
import com.yokoro.moneyplanner.domain.entity.plan.ExpectedIncome
import com.yokoro.moneyplanner.domain.entity.shared.*
import com.yokoro.moneyplanner.domain.repository.ExpectedIncomeRepository
import com.yokoro.moneyplanner.domain.usecase.Either
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.security.InvalidParameterException

class ExpectedIncomeDataStore: ExpectedIncomeRepository, KoinComponent {
    private val database: AppDatabase by inject()

    override fun registerExpectedIncome(expectedIncome: ExpectedIncome): Either<ExpectedIncome> {
        database.expectedIncomeDao().insert(
            LocalExpectedIncome(
                id = 0,
                date = expectedIncome.date.value,
                value = expectedIncome.value.value,
                reason = when(val v = expectedIncome.reason.value) {
                    is Either.Specify -> v.value
                    else -> null
                }
            )
        )

        return Either.Empty()
    }

    override fun removeExpectedIncome(expectedIncome: ExpectedIncome): Either<ExpectedIncome> {
        database.expectedIncomeDao().delete(
            id = expectedIncome.id
        )

        return Either.Empty()
    }

    override fun updateExpectedIncome(expectedIncome: ExpectedIncome): Either<ExpectedIncome> {
        database.expectedIncomeDao().update(
            id = expectedIncome.id,
            date = expectedIncome.date.value,
            value = expectedIncome.value.value,
            reason = when(val v = expectedIncome.reason.value) {
                is Either.Specify -> v.value
                else -> ""
            }
        )

        return Either.Empty()
    }

    override fun getExpectedIncome(range: SearchRange): Either<List<ExpectedIncome>> {
        return Either.Specify(database.expectedIncomeDao().getExpectedIncomeBetween(
            when(val f = range.from) {
                is Either.Specify -> f.value.value
                else -> {
                    when(val t = range.to) {
                        is Either.Specify -> t.value.value
                        else -> throw InvalidParameterException("Parameter is necessary")
                    }
                }
            },
            when(val t = range.to) {
                is Either.Specify -> t.value.value
                else -> {
                    when(val f = range.from) {
                        is Either.Specify -> f.value.value
                        else -> throw InvalidParameterException("Parameter is necessary")
                    }
                }
            }
        ).map { expectedIncome ->
            ExpectedIncome(
                id = expectedIncome.id,
                date = Date(expectedIncome.date),
                reason = Reason(
                    expectedIncome.reason?.let {
                        Either.Specify(it)
                    } ?: Either.Empty()
                ),
                value = Income(expectedIncome.value)
            )
        })
    }

}