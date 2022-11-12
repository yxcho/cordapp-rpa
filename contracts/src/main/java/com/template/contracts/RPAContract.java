package com.template.contracts;

import com.template.states.RPAState;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;



@LegalProseReference(uri = "<prose_contract_uri>")
public class RPAContract implements Contract {

    // Used to identify our contracts when building a transaction
    public static final String RPA_CONTRACT_ID = "com.template.contracts.RPAContract";

    /**
     * The RPAContract can handle three transaction types involving [RPAState]s.
     * - Issuance: Issuing a new [RPAState] on the ledger, which is a bilateral agreement between two parties.
     * - Transfer: Re-assigning the lender/beneficiary.
     * - Settle: Fully or partially settling the [RPAState].
     */
    //Used to indicate the transactions intent
    public interface Commands extends CommandData {
        class Issue implements Commands{}
        class Transfer implements Commands{}
        class Settle implements Commands{}
//        class Issue extends TypeOnlyCommandData implements Commands {
//        }
//
//        class Transfer extends TypeOnlyCommandData implements Commands {
//        }
//
//        class Settle extends TypeOnlyCommandData implements Commands {
//        }

    }

    /**
     * The contracts code for the [RPAContract].
     * The constraints are self documenting so don't require any additional explanation.
     */
    @Override
    public void verify(LedgerTransaction tx) {

        // We can use the requireSingleCommand function to extract command data from transaction.
        final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final Commands commandData = command.getValue();

        /**
         * This command data can then be used inside of a conditional statement to indicate which set of tests we
         * should be performing - we will use different assertions to enable the contracts to verify the transaction
         * for issuing, settling and transferring.
         */
        if (commandData instanceof Commands.Issue)
            verifyIssue(tx);

        else if (commandData instanceof Commands.Transfer)
            verifyTransfer(tx);

        else if (commandData instanceof Commands.Settle)
            verifySettle(tx);

        else
            throw new IllegalArgumentException("Invalid Command");

    }


    private void verifyIssue(LedgerTransaction tx) {
        requireThat(require -> {

            require.using("No inputs should be consumed when issuing an RPA.", tx.getInputStates().size() == 0);
            require.using("Only one output states should be created when issuing an RPA.", tx.getOutputStates().size() == 1);

            RPAState outputState = tx.outputsOfType(RPAState.class).get(0);
            require.using("A newly issued RPA must have a positive amount.", outputState.getAmount() > 0);
            require.using("The lender and borrower cannot have the same identity.", outputState.getLiquidityProvider().getOwningKey() != outputState.getCoreEnterprise().getOwningKey());

            List<PublicKey> signers = tx.getCommand(0).getSigners();
            HashSet<PublicKey> signersSet = new HashSet<>();
            for (PublicKey key : signers) {
                signersSet.add(key);
            }

            List<AbstractParty> participants = tx.getOutputStates().get(0).getParticipants();
            HashSet<PublicKey> participantKeys = new HashSet<>();
            for (AbstractParty party : participants) {
                participantKeys.add(party.getOwningKey());
            }

            require.using("Both lender and borrower together only may sign RPA issue transaction.", signersSet.containsAll(participantKeys) && signersSet.size() == 2);

            return null;
        });

    }


    private void verifyTransfer(LedgerTransaction tx) {
        requireThat(require -> {

            require.using("An RPA transfer transaction should only consume one input states.", tx.getInputStates().size() == 1);
            require.using("An RPA transfer transaction should only create one output states.", tx.getOutputStates().size() == 1);
//
//            // Copy of input with new lender;
//            RPAState inputState = tx.inputsOfType(RPAState.class).get(0);
//            RPAState outputState = tx.outputsOfType(RPAState.class).get(0);
//            RPAState checkOutputState = new RPAState(outputState.getAmount(), inputState.getLender(), outputState.getBorrower(), outputState.getPaid(), outputState.getLinearId());
//
//            require.using("Only the lender property may change.",
//                    (checkOutputState.getAmount() == inputState.getAmount()) && checkOutputState.getLinearId().equals(inputState.getLinearId()) && checkOutputState.getBorrower().equals(inputState.getBorrower()) && (checkOutputState.getPaid() == inputState.getPaid()));
//            require.using("The lender property must change in a transfer.", !outputState.getLender().getOwningKey().equals(inputState.getLender().getOwningKey()));
//
//            Set<PublicKey> listOfParticipantPublicKeys = inputState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toSet());
//            listOfParticipantPublicKeys.add(outputState.getLender().getOwningKey());
//            List<PublicKey> arrayOfSigners = tx.getCommand(0).getSigners();
//            Set<PublicKey> setOfSigners = new HashSet<PublicKey>(arrayOfSigners);
//            require.using("The borrower, old lender and new lender only must sign an RPA transfer transaction", setOfSigners.equals(listOfParticipantPublicKeys) && setOfSigners.size() == 3);
            return null;

        });

    }

    private void verifySettle(LedgerTransaction tx) {
        requireThat(require -> {

            // Check that only one input RPA should be consumed.
            require.using("One input RPA should be consumed when settling an RPA.", tx.getInputStates().size() == 1);
//
//            RPAState inputRPA = tx.inputsOfType(RPAState.class).get(0);
//            int inputAmount = inputRPA.getAmount();
//
//            // Check if there is no more than 1 Output RPA state.
//            require.using("No more than one output RPA should be created", tx.getOutputStates().size() <= 1);
//            if (tx.getOutputStates().size() == 1) {
//                // This means part amount of the obligation is settled.
//                RPAState outputRPA = tx.outputsOfType(RPAState.class).get(0);
//                require.using("Only the paid amount can change during part settlement.",
//                        (outputRPA.getAmount() == inputAmount) && outputRPA.getLinearId().equals(inputRPA.getLinearId()) && outputRPA.getBorrower().equals(inputRPA.getBorrower()) && outputRPA.getLender().equals(inputRPA.getLender()));
//                require.using("The paid amount must increase in case of part settlement of the RPA.", (outputRPA.getPaid() > inputRPA.getPaid()));
//                require.using("The paid amount must be less than the total amount of the RPA", (outputRPA.getPaid() < inputRPA.getAmount()));
//            }
//            Set<PublicKey> listOfParticipantPublicKeys = inputRPA.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toSet());
//            List<PublicKey> arrayOfSigners = tx.getCommand(0).getSigners();
//            Set<PublicKey> setOfSigners = new HashSet<PublicKey>(arrayOfSigners);
//            require.using("Both lender and borrower must sign RPA settle transaction.", setOfSigners.equals(listOfParticipantPublicKeys));

            return null;
        });

    }

}
