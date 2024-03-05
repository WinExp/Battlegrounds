package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.util.Variables;

public class VotePresets {
    public final static VoteSettings START_GAME = new VoteSettings(
            Battlegrounds::onStartGameVoteClosed,
            Battlegrounds::onStartGamePlayerVoted,
            VoteSettings.VoteMode.ALL_ACCEPT,
            Variables.config.cooldown.startGameVoteCooldownTicks
    );
}
