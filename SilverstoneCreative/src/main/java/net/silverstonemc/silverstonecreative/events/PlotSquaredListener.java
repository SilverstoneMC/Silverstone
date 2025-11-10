package net.silverstonemc.silverstonecreative.events;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.world.block.BlockTypes;

@SuppressWarnings("unused")
public class PlotSquaredListener {
    @SuppressWarnings("DataFlowIssue")
    @Subscribe
    public void onPlayerClaimPlot(PlayerClaimPlotEvent event) {
        if (!event.getPlot().canClaim(event.getPlotPlayer())) return;

        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.GRASS_BLOCK.getDefaultState(), 1);
        pattern.add(BlockTypes.MOSS_BLOCK.getDefaultState(), 1);
        pattern.add(BlockTypes.LIME_TERRACOTTA.getDefaultState(), 1);

        event.getPlot().getPlotModificationManager().setComponent(
            "FLOOR",
            pattern,
            event.getPlotPlayer(),
            null);
    }
}
