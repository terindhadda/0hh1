package cas.se3xa3.bitsplease.model;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created on 25/10/2015.
 */
public class GsonWrapper {
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("\\(\\s*(?<row>\\d+)\\s*,\\s*(?<column>\\d+)\\s*\\)");
    private Gson gson;

    public GsonWrapper(boolean prettyPrint) {
        GsonBuilder builder = new GsonBuilder();
        if (prettyPrint) builder.setPrettyPrinting();
        supportBoard(builder);
        supportCoordinate(builder);
        gson = builder.create();
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Wrap the given reader in a lenient json reader.
     * @param reader the reader to wrap.
     * @return the lenient reader
     */
    public JsonReader wrapReader(Reader reader) {
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);
        return jsonReader;
    }

    private void supportBoard(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(Board.class, (JsonSerializer<Board>) (src, typeOfSrc, context) -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("size", src.getSize());
                    JsonObject board = new JsonObject();
                    IntStream.range(0, src.getSize()).forEach(i -> {
                        JsonArray row = new JsonArray();
                        IntStream.range(0, src.getSize())
                                .forEach(j -> row.add(src.isLocked(i, j) ?
                                        Character.toUpperCase(src.getTileAt(i, j).getSerializedChar())
                                                : src.getTileAt(i, j).getSerializedChar()
                                ));
                        board.add(Integer.toString(i), row);
                    });
                    obj.add("contents", board);
                    return obj;
                }
        );
        builder.registerTypeHierarchyAdapter(Board.class, (JsonDeserializer<Board>) (json, typeOfT, context) -> {
                    if (!json.isJsonObject()) throw new JsonParseException("json is not a JsonObject");
                    JsonObject obj = json.getAsJsonObject();
                    if (!obj.has("size")) throw new JsonParseException("json does not have required field 'size'");
                    Board board = new Board(obj.get("size").getAsInt());
                    JsonObject contents = obj.get("contents").getAsJsonObject();
                    IntStream.range(0, board.getSize()).forEach(i -> {
                        JsonArray row = contents.get(Integer.toString(i)).getAsJsonArray();
                        IntStream.range(0, board.getSize()).forEach(j -> {
                            char serializedTile = row.get(j).getAsCharacter();
                            boolean locked = Character.isUpperCase(serializedTile);
                            Tile tile = Tile.deserialize(Character.toLowerCase(serializedTile));
                            if (tile == null) throw new JsonParseException("unknown tile at (" + i + ", " + j + ")");
                            board.setTileAt(i, j, tile);
                            board.setLockAt(i, j, locked);
                        });
                    });
                    return board;
                }
        );
    }

    private void supportCoordinate(GsonBuilder builder) {
        builder.registerTypeHierarchyAdapter(Coordinate.class,
                (JsonSerializer<Coordinate>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(String.format("(%d,%d)", src.getRow(), src.getColumn())));
        builder.registerTypeHierarchyAdapter(Coordinate.class,
                (JsonDeserializer<Coordinate>) (json, typeOfT, context) -> {
                    if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString())
                        throw new JsonParseException("Cannot parse coordinate. It must be a primitive string.");
                    Matcher m = COORDINATE_PATTERN.matcher(json.getAsString());
                    if (!m.matches())
                        throw new JsonParseException("Cannot parse coordinate ('"+json.getAsString()+"'). Doesn't match "+COORDINATE_PATTERN.pattern());
                    return new Coordinate(Integer.parseInt(m.group("row")), Integer.parseInt(m.group("column")));
                }
        );
    }
}
