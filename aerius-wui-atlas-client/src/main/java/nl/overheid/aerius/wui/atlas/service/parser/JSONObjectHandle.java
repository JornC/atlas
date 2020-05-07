package nl.overheid.aerius.wui.atlas.service.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSONObjectHandle {
  private final JSONObject inner;

  public JSONObjectHandle(final JSONObject inner) {
    this.inner = inner;
  }

  public JSONObject getInner() {
    return inner;
  }

  public JSONObjectHandle getObject(final String key) {
    final JSONObject object = getValue(key).isObject();
    if (object == null) {
      throw new IllegalStateException("Wrongly assumed json value to be Object while it was not: [" + key + "] in " + inner);
    }

    return new JSONObjectHandle(object);
  }

  public Optional<JSONObjectHandle> getObjectOptional(final String key) {
    if (has(key) && get(key).isObject()) {
      return Optional.of(getObject(key));
    } else {
      return Optional.empty();
    }
  }

  public String getString(final String key) {
    final JSONString string = getValue(key).isString();
    if (string == null) {
      throw new IllegalStateException("Wrongly assumed json value to be String while it was not: [" + key + "] in " + inner);
    }

    return string.stringValue();
  }

  public Optional<String> getStringOptional(final String key) {
    if (has(key) && get(key).isString()) {
      return Optional.of(getString(key));
    } else {
      return Optional.empty();
    }
  }

  public String getStringOrDefault(final String key, final String devault) {
    try {
      return new JSONObjectHandle(inner).getString(key);
    } catch (final IllegalStateException e) {
      return devault;
    }
  }

  public List<String> getStringArray(final String key) {
    final List<String> lst = new ArrayList<>();
    getArray(key).forEachString(lst::add);
    return lst;
  }

  public List<Integer> getIntegerArray(final String key) {
    final List<Integer> lst = new ArrayList<>();
    getArray(key).forEachInteger(lst::add);
    return lst;
  }

  public JSONArrayHandle getArray(final String key) {
    final JSONArray array = getValue(key).isArray();
    if (array == null) {
      throw new IllegalStateException("Wrongly assumed json value to be an array while it was not: [" + key + "] in " + inner);
    }

    return new JSONArrayHandle(array);
  }

  public JSONValue getValue(final String key) {
    final JSONValue value = inner.get(key);
    if (value == null) {
      throw new IllegalStateException("Did not encounter required field in object: " + key + " from " + inner);
    }

    return value;
  }

  public Double getNumber(final String key) {
    final JSONNumber number = getValue(key).isNumber();
    if (number == null) {
      throw new IllegalStateException("Wrongly assumed json value to be Number while it was not: [" + key + "] in " + inner);
    }

    return number.doubleValue();
  }

  public int getInteger(final String key) {
    return getNumber(key).intValue();
  }

  public long getLong(final String key) {
    return getNumber(key).longValue();
  }

  public Set<String> keySet() {
    return inner.keySet();
  }

  public JSONValueHandle get(final String key) {
    final JSONValue object = inner.get(key);

    if (object == null) {
      throw new IllegalStateException("Did not encounter required item in object: " + key + " from " + inner);
    }

    return new JSONValueHandle(object);
  }

  public boolean getBoolean(final String key) {
    final JSONValue bool = inner.get(key);
    if (bool == null) {
      throw new IllegalStateException("Wrongly assumed json value to be Number while it was not: [" + key + "] in " + inner);
    }

    return bool.isBoolean().booleanValue();
  }

  public boolean has(final String key) {
    return getInner() != null && keySet() != null && keySet().contains(key);
  }

  public Optional<JSONArrayHandle> getArrayOptional(final String key) {
    if (has(key) && get(key).isArray()) {
      return Optional.of(getArray(key));
    } else {
      return Optional.empty();
    }
  }

  public Optional<Boolean> getBooleanOrDefault(final String key, final boolean devault) {
    if (has(key) && get(key).isBoolean()) {
      return Optional.of(getBoolean(key));
    } else {
      return Optional.of(devault);
    }
  }

  public Optional<Double> getNumberOptional(final String key) {
    if (has(key) && get(key).isNumber()) {
      return Optional.of(getNumber(key));
    } else {
      return Optional.empty();
    }
  }

  public Optional<Integer> getIntegerOptional(final String key) {
    if (has(key) && get(key).isNumber()) {
      return Optional.of(getInteger(key));
    } else {
      return Optional.empty();
    }
  }

  public static JSONObjectHandle fromJson(final JSONValue json) {
    return new JSONObjectHandle(json.isObject());
  }

  public static JSONObjectHandle fromText(final String text) {
    return fromJson(JSONParser.parseStrict(text));
  }

  public boolean isNull(final String key) {
    return get(key).isNull();
  }

  public void forEach(final BiConsumer<String, JSONValueHandle> consumer) {
    inner.keySet().forEach(v -> {
      consumer.accept(v, get(v));
    });
  }
}
