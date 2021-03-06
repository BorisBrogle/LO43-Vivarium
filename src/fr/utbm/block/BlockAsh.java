package fr.utbm.block;

import fr.utbm.texture.TextureManager;
import fr.utbm.world.World;

public class BlockAsh extends Block {
	public BlockAsh(float x, float y, World w)
	{
		super(x, y, TextureManager.getTexture(6), w);
		this.blockId = 6;
		this.blockHealth = 100;
		this.maxHealth = 100;
		this.blockType = BlockType.ASH;
		this.isGravitySensitive = false;
		isSolid = true;
	}
	
	public BlockAsh(float x, float y, int bH, World w) {
		super(x, y, TextureManager.getTexture(6), w);
		this.blockId = 6;
		this.maxHealth = 100;
		this.blockHealth = bH;
		this.blockType = BlockType.ASH;
		this.isGravitySensitive = false;
		isSolid = true;
	}

}
