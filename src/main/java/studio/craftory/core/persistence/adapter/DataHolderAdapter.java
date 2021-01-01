package studio.craftory.core.persistence.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.util.Iterator;
import java.util.Map.Entry;
import studio.craftory.core.data.persitanceholders.DataHolder;

public class DataHolderAdapter extends StdDeserializer<DataHolder> {

  public DataHolderAdapter() {
    this(null);
  }

  public DataHolderAdapter(Class<?> vc) {
    super(vc);
  }

  @Override
  public DataHolder deserialize(JsonParser parser, DeserializationContext deserializer) {
    DataHolder dataHolder = new DataHolder();
    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);

    for (Iterator<Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
      Entry<String, JsonNode> element = it.next();

    }
    return dataHolder;
  }
}
