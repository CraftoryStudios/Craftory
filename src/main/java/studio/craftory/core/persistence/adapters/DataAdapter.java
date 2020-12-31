package studio.craftory.core.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;

public interface DataAdapter<K> {

  void store(@NonNull final PersistenceStorage persistenceStorage, final K value, @NonNull final JsonNode node);

  K parse (@NonNull final PersistenceStorage persistenceStorage, @NonNull Object parentObject, @NonNull final JsonNode node);
}
