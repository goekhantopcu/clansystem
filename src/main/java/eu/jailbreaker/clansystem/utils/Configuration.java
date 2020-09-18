package eu.jailbreaker.clansystem.utils;

import com.google.gson.*;
import lombok.Cleanup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path path;
    private JsonObject root;

    public Configuration(File file) {
        this(file.toPath());
    }

    public Configuration(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            this.root = GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            this.root = new JsonObject();
        }
        this.path = path;
    }

    private Configuration(JsonObject root) {
        this.root = root;
    }

    public void set(String key, Object value) {
        if (value instanceof Number) {
            this.root.addProperty(key, ((Number) value));
        } else if (value instanceof Boolean) {
            this.root.addProperty(key, (Boolean) value);
        } else if (value instanceof Character) {
            this.root.addProperty(key, (Character) value);
        } else if (value instanceof JsonElement) {
            this.root.add(key, (JsonElement) value);
        } else if (value instanceof Configuration) {
            this.root.add(key, ((Configuration) value).root);
        } else {
            this.root.addProperty(key, value.toString());
        }
    }

    public void save() {
        try {
            if (Files.notExists(this.path)) {
                @Cleanup
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("messages.json");
                if (in == null) {
                    in = new ByteArrayInputStream("{}".getBytes());
                }
                Files.copy(
                        in,
                        this.path, StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public boolean hasEntry(String key) {
        return this.root.has(key);
    }

    public Configuration getSection(String key) {
        if (!this.hasEntry(key)) {
            return this;
        }
        return new Configuration(this.root.get(key).getAsJsonObject());
    }

    public JsonElement getElement(String key) {
        if (!this.hasEntry(key)) {
            return new JsonPrimitive("unknown property");
        }
        return this.root.get(key);
    }

    public <T> T get(String key, Class<T> tClass) {
        return GSON.fromJson(this.getElement(key), tClass);
    }

    public String getString(String key) {
        if (!this.hasEntry(key)) {
            return "unknown property";
        }
        return this.getElement(key).getAsString();
    }

    public int getInt(String key) {
        if (!this.hasEntry(key)) {
            return 0;
        }
        return this.getElement(key).getAsInt();
    }

    public byte getByte(String key) {
        if (!this.hasEntry(key)) {
            return 0;
        }
        return this.getElement(key).getAsByte();
    }

    public short getShort(String key) {
        if (!this.hasEntry(key)) {
            return 0;
        }
        return this.getElement(key).getAsShort();
    }

    public long getLong(String key) {
        if (!this.hasEntry(key)) {
            return 0;
        }
        return this.getElement(key).getAsLong();
    }

    public boolean getBoolean(String key) {
        if (!this.hasEntry(key)) {
            return false;
        }
        return this.getElement(key).getAsBoolean();
    }
}
