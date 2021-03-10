# Papyrus world storage v1

World storage concept for 2D, voxel based worlds

> Disclaimer: This format is not completely mature yet.

## General

Everything is stored in little endian

Representation of uuids:

```
UUID {
  int64 mostSigBits,
  int64 leastSigBits
}
```

## Structure

1. Header (6 bytes) (See [Header](#header))
2. Block index table (min. 2 bytes) (See [Block index](#block-index))
3. Game objects (min. 2 bytes) (See [Game objects](#game-objects))
4. Seed (4 bytes)
5. Chunks (min. 4 bytes) (See [Chunks](#chunks))

```
Papyrus {
  Header,
  BlockIndex,
  GameObject[],
  int32 seed,
  Chunk[]
}
```

## Header

First 4 bytes always have to be `['P', 'Y', 'R', 'S']`

Following 2 bytes represent the version (eg `1`)

```
Header {
  byte[4] magic,
  int16 version
}
```

## Block index

The first 2 bytes of the block index represent the amount of elements. The first element is always the Air block.

A single block index element looks like this:

```
BlockIndexElement {
  int32 index,
  int16 len,
  byte[len] blockId
}
```

So, the block index looks like this:

```
BlockIndex {
  int16 len,
  BlockIndexElement[len]
}
```

## Game objects

The first 2 bytes of the game objects section represent the amount of objects.

A game object can be anything that is not a block. The player for example is a game object.

Each game object may store as much data as they would like.

```
# Common struct for every game object
GameObject {
  UUID type,            # Identifier (eg the player in Flatcraft has the UUID 00000000-0000-0000-0000-000000000000)
  int32 posX,
  int32 posY
}
```

### Player

```
# Extends the common game object struct
PlayerGameObject {
  int16 len,
  InventoryElement[len] inventory
}

# Inventory element
InventoryElement {
  int32 block,          # Pointer to block index
  int16 amount
}
```

## Chunks

The first 2 bytes of the chunks section represent the amount of chunks.

A chunk is a 16 x 64 slice of the world.

```
Chunk {
  int32 x,              # Coordinate
  int16 len,            # Pretty much always 16*64 (at least in Flatcraft)
  int16[len] blocks     # Pointers to block index
}
```