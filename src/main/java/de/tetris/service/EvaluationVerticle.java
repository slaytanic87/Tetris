package de.tetris.service;

import de.tetris.model.TetrisField;
import de.tetris.model.block.BlockType;
import de.tetris.model.data.Pos;
import de.tetris.utils.MetricCalculatorUtils;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class EvaluationVerticle extends AbstractVerticle {

    public static final String EVENT_BLOCK_DISPLACE_VEC = "event.blockDisplaceVec";
    public static final String EVENT_FIELD_BRICK_LEVEL_VEC = "event.fieldBrickLevelVec";
    public static final String EVENT_BLOCK_TYPE = "event.blockType";
    public static final String EVENT_CALC_POSITION = "event.calcPosition";

    private int[] blockDisplaceVecMetric;
    private int[] blockDisplaceVec;
    private int[] fieldBrickLevelVec;
    private int[] fieldBrickLevelVecMetric;
    private List<Pos> optimalPositions;
    private BlockType blockType;
    private EvaluationStateStore stateStore = new EvaluationStateStore();

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer(EVENT_BLOCK_DISPLACE_VEC, this::handleBlockDisplaceVec);
        vertx.eventBus().consumer(EVENT_FIELD_BRICK_LEVEL_VEC, this::handleFieldBrickLevelVec);
        vertx.eventBus().consumer(EVENT_BLOCK_TYPE, this::handleBlockType);
        vertx.eventBus().consumer(EVENT_CALC_POSITION, this::handleCalcPosition);
    }

    private void handleBlockType(Message<String> message) {
        try {
            String messageContent = message.body();
            log.debug("handleBlockType: {}", messageContent);
            if (messageContent == null || messageContent.isEmpty()) {
                throw new RuntimeException("Block type is empty!");
            }
            stateStore.registerNextState(EvaluationStateStore.State.SET_BLOCK_DISPL_VEC);
            blockType = Json.decodeValue(messageContent, BlockType.class);
        } catch (Exception e) {
            message.fail(-1, e.getMessage());
        }
    }

    private void handleBlockDisplaceVec(Message<String> message) {
        try {
            String messageContent = message.body();
            log.debug("handleBlockDisplaceVec: {}", messageContent);
            if (messageContent == null || messageContent.isEmpty()) {
                throw new RuntimeException("Block displacement vector is empty!");
            }
            blockDisplaceVec = Json.decodeValue(messageContent, int[].class);
            stateStore.registerNextState(EvaluationStateStore.State.SET_FIELD_LEVEL_VEC);
            blockDisplaceVecMetric = MetricCalculatorUtils.calcMetricBrickDisplDiffVec(blockDisplaceVec);
            log.debug("blockDisplaceVecMetric: {}", blockDisplaceVecMetric);
        } catch (Exception e) {
            message.fail(-1, e.getMessage());
        }
    }

    private void handleFieldBrickLevelVec(Message<String> message) {
        try {
            String messageContent = message.body();
            log.debug("handleFieldBrickLevelVec: {}", messageContent);
            if (messageContent == null || messageContent.isEmpty()) {
                throw new RuntimeException("Field brick level vector is empty!");
            }
            fieldBrickLevelVec = Json.decodeValue(messageContent, int[].class);
            stateStore.registerNextState(EvaluationStateStore.State.CALC_POSITION);
            if (blockType.equals(BlockType.I) && blockDisplaceVecMetric.length == 1) {
                fieldBrickLevelVecMetric = MetricCalculatorUtils.calcMetricBrickFieldLevelExtDiffVec(fieldBrickLevelVec);
            } else {
                fieldBrickLevelVecMetric = MetricCalculatorUtils.calcMetricBrickFieldLevelDiffVec(fieldBrickLevelVec);
            }
            log.debug("fieldBrickLevelVecMetric: {}", fieldBrickLevelVecMetric);
        } catch (Exception e) {
            message.fail(-1, e.getMessage());
        }
    }

    private void handleCalcPosition(Message<String> message) {
        try {
            String messageContent = message.body();
            if (messageContent == null || messageContent.isEmpty()) {
                throw new RuntimeException("Could not determine block height!");
            }
            log.debug("block height: {}", messageContent);

            List<Integer> optimalXpositions;
            stateStore.registerNextState(EvaluationStateStore.State.SET_BLOCKTYPE);
            if (blockType.equals(BlockType.I) && blockDisplaceVecMetric.length == 1) {
                optimalXpositions = MetricCalculatorUtils.calcOptimumPlacePosVerBlockI(fieldBrickLevelVecMetric);
            } else {
                optimalXpositions = MetricCalculatorUtils.calcOptimumPlacePositions(fieldBrickLevelVecMetric,
                        blockDisplaceVecMetric);
            }
            log.debug("handleCalcPosition x: {}", optimalXpositions);
            optimalPositions = calcCoordinates(optimalXpositions, Integer.valueOf(messageContent));
            blockType = null;
            blockDisplaceVecMetric = null;
            fieldBrickLevelVecMetric = null;
            fieldBrickLevelVec = null;
            blockDisplaceVec = null;
            Pos chosenPos = chooseValue(optimalPositions);
            message.reply(Json.encode(chosenPos));
        } catch (Exception e) {
            message.fail(-1, e.getMessage());
        }
    }

    private Pos chooseValue(List<Pos> optimalPositions) {
        Random random = new Random();
        int randomPos = random.nextInt(optimalPositions.size());
        return optimalPositions.get(randomPos);
    }

    private List<Pos> calcCoordinates(List<Integer> xCoordinates, Integer blockHeight) {
        List<Pos> optimalPositions = new ArrayList<>();
        for (Integer x : xCoordinates) {
            int calculatedPosY = TetrisField.ROWS - fieldBrickLevelVec[x] - blockHeight + blockDisplaceVec[0];
            int y = fieldBrickLevelVec[x] == 0 ? TetrisField.ROWS - fieldBrickLevelVec[x] - blockHeight : calculatedPosY;
            Pos coordinate = new Pos(x, y);
            optimalPositions.add(coordinate);
        }
        log.debug("calcCoordinates [x, y]: {}", optimalPositions);
        return optimalPositions;
    }

}
