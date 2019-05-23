package sudoku.command;

import lombok.Getter;
import sudoku.exceptions.NoAvailableSolutionException;
import sudoku.model.Sudoku;
import sudoku.strategy.Resolvable;
import sudoku.strategy.StrategyFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An invoker that lets the user to choose which strategy is to be used in any state to get the next state.
 * User can return to previous state and even choose new strategy to get to the different state
 */
@Getter
public class ManualInvoker implements Invoker {
    private StrategyFactory strategyFactory = new StrategyFactory();
    private List<Command> commands = new LinkedList<>();
    private List<Resolvable> strategies = new ArrayList<>();
    private int currentStep = 0;
    private Sudoku sudoku;

    /**
     * Constructor method that gets the current sudoku and sets default strategy
     *
     * @param sudoku    sudoku that is the current sudoku
     */
    public ManualInvoker(Sudoku sudoku) {
        this.sudoku = sudoku;
//      default strategy will be NakedSingleCell
        this.strategies.add(strategyFactory.createNakedSingleInACellStrategy());
    }

    public void setStrategies(Resolvable ... useStrategies) {
        this.strategies = new ArrayList<>();
        this.strategies.addAll(Arrays.asList(useStrategies));
    }

    /**
     * Method that helps navigate between states in backward direction.
     *
     * @return      command containing information about previous state
     */
    @Override
    public Command getPreviousState() {
        Command command = null;

        if (currentStep > 0) {
            currentStep--;
            commands.remove(commands.size()-1);
            command = commands.get(commands.size() - 1);
        }

        return command;
    }

    /**
     * Method that generates a new command containing next state in sequence. Returned Command is generated by applying
     * of the strategy by the user on current state of the sudoku
     *
     * @return      command containing information about next state
     */
    @Override
    public Command getNextState() throws NoAvailableSolutionException {

        CommandPicker command = new CommandPicker(strategies.get(0), sudoku);

        if (!sudoku.isSudokuResolved()) {
            sudoku = command.execute();
            currentStep++;
            command = new CommandPicker(strategies.get(0), sudoku.copy());
            commands.add(command);
        }

        return command;
    }

    /**
     * Method that directly sets strategy that is to be used to generate next state and then generates command
     * containing the next state
     *
     * @param strategy  resolvable that is strategy chosen by user to be used to get the next state
     * @return          a command containing the next state achieved by using chosen strategy
     */
    public Command getNextState(Resolvable strategy) throws NoAvailableSolutionException {
        setStrategies(strategy);

        return getNextState();
    }
}