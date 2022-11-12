//package com.template.schema;
//
//import com.google.common.collect.ImmutableList;
//import net.corda.core.schemas.MappedSchema;
//import net.corda.core.schemas.PersistentState;
//import org.hibernate.annotations.Type;
//
//import javax.annotation.Nullable;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Table;
//import java.util.UUID;
//
//public class RPASchema1 extends MappedSchema {
//    public RPASchema1() {
////        super(RPASchema.class, 1, Arrays.asList(PersistentRPA.class));
//                super(RPASchema.class, 1, ImmutableList.of(PersistentRPA.class));
//
//    }
//
//    @Nullable
//    @Override
//    public String getMigrationResource() {
//        return "rpa.changelog-master";
//    }
//
//    @Entity
//    @Table(name = "rpa_states")
//    public static class PersistentRPA extends PersistentState {
//        @Column(name = "amount") private final Integer amount;
//        @Column(name = "discountRate") private final Float discountRate;
//        @Column(name = "tenor") private final Float tenor;
//
//
//        @Column(name = "liquidityProvider") private final String liquidityProvider;
//        @Column(name = "coreEnterprise") private final String coreEnterprise;
//        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
//
//        // Default constructor required by hibernate.
//        public PersistentRPA() {
//            this.amount = 1;
//            this.discountRate = Float.valueOf(0);
//            this.tenor = Float.valueOf(0);
//            this.liquidityProvider = null;
//            this.coreEnterprise = null;
//            this.linearId = null;
//        }
//        public PersistentRPA(Integer amount, Float discountRate, Float tenor, String liquidityProvider, String coreEnterprise, UUID linearId) {
//            this.amount = amount;
//            this.discountRate = discountRate;
//            this.tenor = tenor;
//            this.liquidityProvider = liquidityProvider;
//            this.coreEnterprise = coreEnterprise;
//            this.linearId = linearId;
//        }
//
//
//
//        public int getAmount() {
//            return amount;
//        }
//        public float getDiscountRate(){return discountRate;}
//        public float getTenor(){return tenor;}
//        public String getLiquidityProvider() {
//            return liquidityProvider;
//        }
//
//        public String getCoreEnterprise() {
//            return coreEnterprise;
//        }
//
//
//        public UUID getId() {
//            return linearId;
//        }
//    }
//}
