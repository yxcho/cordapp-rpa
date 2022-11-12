package com.template.flows;
//package net.corda.samples.obligation.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.RPAContract;
import com.template.states.RPAState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;

import static net.corda.core.contracts.ContractsDSL.requireThat;


/**
 * This flow allows two parties (the [Initiator] and the [Acceptor]) to come to an agreement about the IOU encapsulated
 * within an [IOUState].
 *
 * In our simple example, the [Acceptor] always accepts a valid IOU.
 *
 * These flows have deliberately been implemented by using only the call() method for ease of understanding. In
 * practice we would recommend splitting up the various stages of the flow into sub-routines.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
public class RPAIssueFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final int amount;
        private final float discountRate;
        private final float tenor;
        private final Party liquidityProvider;
        private final Party coreEnterprise;




        private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction based on new RPA.");
        private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints.");
        private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our (bank) private key.");
        private final ProgressTracker.Step GATHERING_SIGS = new ProgressTracker.Step("Gathering the core enterprise's signature.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };
        private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };

        // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
        // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
        // function.
        private final ProgressTracker progressTracker = new ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        );






        public Initiator(int amount, float discountRate, float tenor, Party liquidityProvider, Party coreEnterprise) {

            this.amount = amount;
            this.discountRate = discountRate;
            this.tenor = tenor;
            this.liquidityProvider = liquidityProvider;
            this.coreEnterprise = coreEnterprise;

        }


        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }


        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {



            // Obtain a reference to a notary we wish to use.
            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
            System.out.println(notary);
            // Stage 1.
            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            // Generate an unsigned transaction.
            Party me = getOurIdentity();
//            System.out.println(amount);
//            System.out.println(discountRate);
//            System.out.println(tenor);
//            System.out.println(me);
//            System.out.println(coreEnterprise);
//

            RPAState rpaState = new RPAState(amount, discountRate, tenor, me, coreEnterprise, new UniqueIdentifier());
            final Command<RPAContract.Commands.Issue> txCommand = new Command<>(
                    new RPAContract.Commands.Issue(),
                    ImmutableList.of(rpaState.getLiquidityProvider().getOwningKey(), rpaState.getCoreEnterprise().getOwningKey()));
//                    Arrays.asList(rpaState.getLiquidityProvider().getOwningKey(), rpaState.getCoreEnterprise().getOwningKey()));
            System.out.println(txCommand);
            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(rpaState, RPAContract.RPA_CONTRACT_ID)
                    .addCommand(txCommand);
            System.out.println(txBuilder);


            // Stage 2.
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Stage 3.
            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Stage 4.
            progressTracker.setCurrentStep(GATHERING_SIGS);
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);

            // Send the state to the counterparty (coreEnterprise) to get their signature using corda subFlow
            FlowSession otherPartySession = initiateFlow(coreEnterprise);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.Companion.tracker()));

            System.out.println(fullySignedTx.getId());

            return fullySignedTx;
            // Stage 5.
//            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            // Notarise and record the transaction in both parties' vaults.
//            return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));

//            return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));















//
//
//            // Step 1. Get a reference to the notary service on our network and our key pair.
//
//            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config
//             */
//            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
//            if (notary == null) {
//                throw new FlowException("The desired notary is not known");
//            }
//
//            // Generate an unsigned transaction
//            Party me = getOurIdentity();
//            RPAState state = new RPAState(amount, discountRate, tenor, me, coreEnterprise, new UniqueIdentifier());
//            // Step 2. Create a new issue command.
//            // Remember that a command is a CommandData object and a list of CompositeKeys
//            List<PublicKey> listOfKeys = new ArrayList<>();
//            listOfKeys.add(state.getLiquidityProvider().getOwningKey());
//            listOfKeys.add(state.getCoreEnterprise().getOwningKey());
//            final Command<Issue> issueCommand = new Command<>(new Issue(), listOfKeys);
//
//            // Step 3. Create a new TransactionBuilder object.
//            final TransactionBuilder builder = new TransactionBuilder(notary);
//
//            // Step 4. Add the rpa as an output states, as well as a command to the transaction builder.
//            builder.addOutputState(state, RPAContract.RPA_CONTRACT_ID);
//            builder.addCommand(issueCommand);
//
//            // Step 5. Verify and sign it with our KeyPair.
//            builder.verify(getServiceHub());
//            final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);
//
//            // Step 6. Collect the other party's signature using the CollectSignaturesFlow.Each required signer will need to
//            // respond by invoking its own SignTransactionFlow subclass to check the transaction (by implementing the checkTransaction method)
//            // and provide their signature if they are satisfied.
//            List<Party> otherParties = new ArrayList<Party>();
//            otherParties.add(state.getLiquidityProvider());
//            otherParties.add(state.getCoreEnterprise());
//            otherParties.remove(getOurIdentity());
//
//            // Collect all of the required signatures from other Corda nodes using the CollectSignaturesFlow
//            List<FlowSession> sessions = new ArrayList<>();
//            for (Party otherParty : otherParties) {
//                sessions.add(initiateFlow(otherParty));
//            }
//            SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, sessions));
//
//            // Step 7. Assuming no exceptions, we can now finalise the transaction
//            return subFlow(new FinalityFlow(stx, sessions));
        }
    }



    @InitiatedBy(Initiator.class)
    public static class Acceptor extends FlowLogic<SignedTransaction> {

        private final FlowSession otherPartySession;

        public Acceptor(FlowSession otherPartySession) {
            this.otherPartySession = otherPartySession;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
//            the responder (coreEnterprise) needs to verify the transaction before signing it
            class SignTxFlow extends SignTransactionFlow {

                public SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
//                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("This must be a transaction of type RPAState.", output instanceof RPAState);
                        RPAState rpa = (RPAState) output;
                        require.using("RPA amount should be positive.", rpa.getAmount() > 0);
                        return null;
                    });
                }
            }
            final SignTxFlow signTxFlow = new SignTxFlow(otherPartySession, SignTransactionFlow.Companion.tracker());
            System.out.println("RPAIssue Acceptor flow: " + signTxFlow);
            final SecureHash txId = subFlow(signTxFlow).getId();
            System.out.println(txId);
            return subFlow(new ReceiveFinalityFlow(otherPartySession, txId));
        }
    }

}
