package com.template.states;

import com.template.contracts.RPAContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;

import java.util.Arrays;
import java.util.List;



@BelongsToContract(RPAContract.class)
public class RPAState implements LinearState, QueryableState {
//    public class RPAState implements ContractState, LinearState, QueryableState {

    private final Integer amount;
    private final Float discountRate;
    private final Float tenor;
    private final Party liquidityProvider;
    private final Party coreEnterprise;
    private final UniqueIdentifier linearId;

    @ConstructorForDeserialization
    public RPAState(Integer amount,
                    Float discountRate,
                    Float tenor,
                    Party liquidityProvider,
                    Party coreEnterprise,
                    UniqueIdentifier linearId)
    {
        this.amount = amount;
        this.discountRate = discountRate;
        this.tenor = tenor;
        this.liquidityProvider = liquidityProvider;
        this.coreEnterprise = coreEnterprise;
        this.linearId = linearId;
    }


    public Integer getAmount() {
        return amount;
    }

    public Float getDiscountRate() {
        return discountRate;
    }

    public Float getTenor() {
        return tenor;
    }

    public Party getLiquidityProvider() {
        return liquidityProvider;
    }

    public Party getCoreEnterprise() {
        return coreEnterprise;
    }

    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    /**
     * This method will return a list of the nodes which can "use" this states in a valid transaction. In this case, the
     * lender or the borrower.
     */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(liquidityProvider, coreEnterprise);
    }


    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
//        if (schema instanceof RPASchema1) {
//            return new RPASchema1.PersistentRPA(
//                    this.amount,
//                    this.discountRate,
//                    this.tenor,
//                    this.liquidityProvider.getName().toString(),
//                    this.coreEnterprise.getName().toString(),
//                    this.linearId.getId());
//        } else {
//            throw new IllegalArgumentException("Unrecognised schema $schema");
//        }
        return null;
    }

    @Override
    public Iterable<MappedSchema> supportedSchemas() {
//        return ImmutableList.of(new RPASchema1());
        return null;
    }

    @Override
    public String toString() {
        return String.format("RPAState(amount=%s, discountRate=%s, tenor=%s,  liquidityProvider=%s, coreEnterprise=%s, linearId=%s)", amount, discountRate, tenor, liquidityProvider, coreEnterprise, linearId);
    }
}
