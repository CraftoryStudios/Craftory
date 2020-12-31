package studio.craftory.core.persistence.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import studio.craftory.core.data.persitanceholders.DataHolder;
import studio.craftory.core.persistence.PersistenceStorage;

public class DataHolderAdapter implements DataAdapter<DataHolder> {

  @Override
  public void store(PersistenceStorage persistenceStorage, DataHolder value, JsonNode node) {

  }

  @Override
  public DataHolder parse(@NonNull PersistenceStorage persistenceStorage, @NonNull Object parentObject, @NonNull JsonNode node) {
    return null;
  }
}
