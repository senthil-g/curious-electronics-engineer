package org.sen.webapp.mapreduce;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.MapJob;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapSettings;
import com.google.appengine.tools.mapreduce.MapSpecification;
import com.google.appengine.tools.mapreduce.inputs.ConsecutiveLongInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;

import java.util.logging.Logger;

public class CreateEntitiesMapJob extends Job0<Void> {

    private static final long serialVersionUID = 6725038763886885189L;
    private static final Logger log = Logger.getLogger(CreateEntitiesMapJob.class.getName());

    private final String datastoreType;
    private final int shardCount;
    private final int entities;
    private final int bytesPerEntity;

    public CreateEntitiesMapJob(String datastoreType, int shardCount, int entities,
                                int bytesPerEntity) {
        this.datastoreType = datastoreType;
        this.shardCount = shardCount;
        this.entities = entities;
        this.bytesPerEntity = bytesPerEntity;
    }

    private static class LogResults extends
            Job1<Void, MapReduceResult<List<List<KeyValue<String, Long>>>>> {

        private static final long serialVersionUID = 131906664096202890L;

        @Override
        public Value<Void> run(MapReduceResult<List<List<KeyValue<String, Long>>>> mrResult)
                throws Exception {
            List<String> mostPopulars = new ArrayList<>();
            long mostPopularCount = -1;
            for (List<KeyValue<String, Long>> countList : mrResult.getOutputResult()) {
                for (KeyValue<String, Long> count : countList) {
                    log.info("Character '" + count.getKey() + "' appeared " + count.getValue() + " times");
                    if (count.getValue() < mostPopularCount) {
                        continue;
                    }
                    if (count.getValue() > mostPopularCount) {
                        mostPopulars.clear();
                        mostPopularCount = count.getValue();
                    }
                    mostPopulars.add(count.getKey());
                }
            }
            if (!mostPopulars.isEmpty()) {
                log.info("Most popular characters: " + mostPopulars);
            }
            return null;
        }
    }

    @Override
    public FutureValue<Void> run() throws Exception {
        MapSettings settings = getSettings();
        FutureValue<MapReduceResult<Void>> futureValue = futureCall(new MapJob<>(getCreationJobSpec(bytesPerEntity, entities, shardCount), settings));
        return futureValue;
    }

    private MapSettings getSettings() {
        // [START mapSettings]
        MapSettings settings = new MapSettings.Builder()
                .setWorkerQueueName("mapreduce-workers")
                .setModule("mapreduce")
                .build();
        // [END mapSettings]
        return settings;
    }

    private MapSpecification<Long, Entity, Void> getCreationJobSpec(int bytesPerEntity, int entities,
                                                                    int shardCount) {
        // [START mapSpec]
        MapSpecification<Long, Entity, Void> spec = new MapSpecification.Builder<>(
                new ConsecutiveLongInput(0, entities, shardCount),
                new EntityCreator(datastoreType, bytesPerEntity),
                new DatastoreOutput())
                .setJobName("Create MapReduce entities")
                .build();
        // [END mapSpec]
        return spec;
    }
}
