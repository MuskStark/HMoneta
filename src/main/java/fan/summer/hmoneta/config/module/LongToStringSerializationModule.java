package fan.summer.hmoneta.config.module;

import com.fasterxml.jackson.core.JsonGenerator;
   import com.fasterxml.jackson.databind.JsonSerializer;
   import com.fasterxml.jackson.databind.SerializerProvider;
   import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class LongToStringSerializationModule extends SimpleModule {

       public LongToStringSerializationModule() {
           addSerializer(Long.class, new LongToStringSerializer());
       }

       private static class LongToStringSerializer extends JsonSerializer<Long> {
           @Override
           public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
               gen.writeString(value.toString());
           }
       }
   }
   