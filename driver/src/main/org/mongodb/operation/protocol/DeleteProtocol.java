/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation.protocol;

import org.mongodb.CommandResult;
import org.mongodb.Document;
import org.mongodb.Encoder;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.connection.BufferProvider;
import org.mongodb.connection.Channel;
import org.mongodb.connection.ServerDescription;
import org.mongodb.diagnostics.Loggers;
import org.mongodb.operation.Remove;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;

public class DeleteProtocol extends WriteProtocol {
    private static final Logger LOGGER = Loggers.getLogger("protocol.delete");

    private final List<Remove> deletes;
    private final Encoder<Document> queryEncoder;

    public DeleteProtocol(final MongoNamespace namespace, final WriteConcern writeConcern, final List<Remove> deletes,
                          final Encoder<Document> queryEncoder, final BufferProvider bufferProvider,
                          final ServerDescription serverDescription, final Channel channel, final boolean closeChannel) {
        super(namespace, bufferProvider, writeConcern, serverDescription, channel, closeChannel);
        this.deletes = deletes;
        this.queryEncoder = queryEncoder;
    }

    @Override
    public CommandResult execute() {
        LOGGER.fine(format("Deleting documents from namespace %s on connection [%s] to server %s", getNamespace(),
                getChannel().getId(), getChannel().getServerAddress()));
        CommandResult commandResult = super.execute();
        LOGGER.fine("Delete completed");
        return commandResult;
    }

    @Override
    protected RequestMessage createRequestMessage(final MessageSettings settings) {
        return new DeleteMessage(getNamespace().getFullName(), deletes, queryEncoder, settings);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
