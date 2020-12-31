package studio.craftory.core.persistence.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import studio.craftory.core.persistence.PersistenceStorage;

public interface DataAdapter<K> {

  void store(@NonNull final PersistenceStorage persistenceStorage, final K value, @NonNull final JsonNode node);

  K parse (@NonNull final PersistenceStorage persistenceStorage, @NonNull Object parentObject, @NonNull final JsonNode node);
}
