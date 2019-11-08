package de.tetris.service;

import lombok.Data;

@Data
class EvaluationStateStore {

    enum State {
        SET_BLOCKTYPE,
        SET_BLOCK_DISPL_VEC,
        SET_FIELD_LEVEL_VEC,
        CALC_POSITION
    }

    private State stateBefore = State.SET_BLOCKTYPE;

    public void registerNextState(State nextState) {
        switch (stateBefore) {
            case SET_BLOCKTYPE:
                if (!nextState.equals(State.SET_BLOCK_DISPL_VEC)) {
                    throw new RuntimeException("Expected next state is " + State.SET_BLOCK_DISPL_VEC
                            + " but got " + nextState);
                }
                stateBefore = nextState;
                break;
            case SET_BLOCK_DISPL_VEC:
                if (!nextState.equals(State.SET_FIELD_LEVEL_VEC)) {
                    throw new RuntimeException("Expected next state is " + State.SET_FIELD_LEVEL_VEC
                            + " but got " + nextState);
                }
                stateBefore = nextState;
                break;
            case SET_FIELD_LEVEL_VEC:
                if (!nextState.equals(State.CALC_POSITION)) {
                    throw new RuntimeException("Expected next state is " + State.CALC_POSITION
                            + " but got " + nextState);
                }
                stateBefore = nextState;
                break;
            case CALC_POSITION:
                if (!nextState.equals(State.SET_BLOCKTYPE)) {
                    throw new RuntimeException("Expected next state is " + State.SET_BLOCKTYPE
                            + " but got " + nextState);
                }
                stateBefore = nextState;
                break;
            default:
                throw new RuntimeException("Unexpected next state: " + nextState + " for current state: "
                        + stateBefore);
        }
    }

}
