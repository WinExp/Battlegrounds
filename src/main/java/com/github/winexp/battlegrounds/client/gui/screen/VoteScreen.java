package com.github.winexp.battlegrounds.client.gui.screen;

import com.github.winexp.battlegrounds.client.util.ClientConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class VoteScreen extends Screen {
    public VoteScreen() {
        super(Text.literal("Vote Screen"));
    }

    @Override
    protected void init() {
        ButtonWidget acceptButton = ButtonWidget
                .builder(Text.translatable("gui.battlegrounds.vote.accept"),
                        button -> {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeEnumConstant(VoteMode.ACCEPT);
                            ClientPlayNetworking.send(ClientConstants.VOTE_PACKET_ID, buf);
                        })
                .dimensions(this.width / 2 - 65, this.height / 2 + 50, 60, 20)
                .build();
        ButtonWidget denyButton = ButtonWidget
                .builder(Text.translatable("gui.battlegrounds.vote.deny"),
                        button -> {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeEnumConstant(VoteMode.DENY);
                            ClientPlayNetworking.send(ClientConstants.VOTE_PACKET_ID, buf);
                        })
                .dimensions(this.width / 2 + 5, this.height / 2 + 50, 60, 20)
                .build();
        this.addDrawableChild(acceptButton);
        this.addDrawableChild(denyButton);

    }

    private enum VoteMode {
        ACCEPT, DENY
    }
}
