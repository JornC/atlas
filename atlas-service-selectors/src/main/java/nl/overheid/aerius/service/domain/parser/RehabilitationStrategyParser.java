package nl.overheid.aerius.service.domain.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;

import nl.overheid.aerius.service.domain.ServiceSelector;

public class RehabilitationStrategyParser {
  public static List<ServiceSelector> parseSelectors(final Optional<JsonNode> json) {
    final List<ServiceSelector> selectors = new ArrayList<>();

    json.ifPresent(v -> {
      final JSONArray habitats = v.getArray();
      for (int i = 0; i < habitats.length(); i++) {
        final JSONObject habitat = habitats.getJSONObject(i);

        final ServiceSelector selector = new ServiceSelector();
        final String code = habitat.getString("code");
        final String name = habitat.getJSONObject("rehabilitationStrategyInfo").getString("description");

        selector.setName(name);
        selector.setValue(code);

        final Map<String, String> tags = new HashMap<>();
        tags.put("id", String.valueOf(habitat.getInt("id")));

        selector.setTags(tags);

        selectors.add(selector);
      }
    });

    return selectors;
  }
}