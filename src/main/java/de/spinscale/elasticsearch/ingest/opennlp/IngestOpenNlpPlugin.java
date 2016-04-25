/*
 * Copyright [2016] [Alexander Reelsen]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.spinscale.elasticsearch.ingest.opennlp;

import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.node.NodeModule;
import org.elasticsearch.plugins.Plugin;

import java.io.IOException;
import java.nio.file.Path;

public class IngestOpenNlpPlugin extends Plugin {

    public static final Setting<String> MODEL_NAME_FILE_SETTING=
            new Setting<>("ingest.opennlp.model.file.name", "en-ner-person.bin", (value) -> value, Setting.Property.NodeScope);
    public static final Setting<String> MODEL_LOCATION_FILE_SETTING=
            new Setting<>("ingest.opennlp.model.file.location", "en-ner-location.bin", (value) -> value, Setting.Property.NodeScope);
    public static final Setting<String> MODEL_DATE_FILE_SETTING=
            new Setting<>("ingest.opennlp.model.file.date", "en-ner-date.bin", (value) -> value, Setting.Property.NodeScope);

    @Override
    public String name() {
        return "ingest-opennlp";
    }

    @Override
    public String description() {
        return "Ingest processor that uses OpenNLP for named entity extraction";
    }

    public void onModule(SettingsModule settingsModule) {
        settingsModule.registerSetting(MODEL_DATE_FILE_SETTING);
        settingsModule.registerSetting(MODEL_LOCATION_FILE_SETTING);
        settingsModule.registerSetting(MODEL_NAME_FILE_SETTING);
    }

    public void onModule(NodeModule nodeModule) throws IOException {
        Path configDirectory = nodeModule.getNode().getEnvironment().configFile().resolve(name());

        OpenNlpService openNlpService = new OpenNlpService(configDirectory, nodeModule.getNode().settings());
        openNlpService.start();

        nodeModule.registerProcessor(OpenNlpProcessor.TYPE,
                (templateService, registry) -> new OpenNlpProcessor.Factory(openNlpService));
    }

}