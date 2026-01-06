/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.ByteString
 *  com.google.protobuf.CodedInputStream
 *  com.google.protobuf.ExtensionRegistryLite
 *  com.google.protobuf.GeneratedMessageLite
 *  com.google.protobuf.GeneratedMessageLite$Builder
 *  com.google.protobuf.GeneratedMessageLite$DefaultInstanceBasedParser
 *  com.google.protobuf.GeneratedMessageLite$MethodToInvoke
 *  com.google.protobuf.InvalidProtocolBufferException
 *  com.google.protobuf.MessageLite
 *  com.google.protobuf.MessageLiteOrBuilder
 *  com.google.protobuf.Parser
 */
package net.runelite.client.party;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

final class Party {
    private Party() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static final class S2C
    extends GeneratedMessageLite<S2C, Builder>
    implements S2COrBuilder {
        private int msgCase_ = 0;
        private Object msg_;
        public static final int JOIN_FIELD_NUMBER = 1;
        public static final int PART_FIELD_NUMBER = 2;
        public static final int DATA_FIELD_NUMBER = 3;
        private static final S2C DEFAULT_INSTANCE;
        private static volatile Parser<S2C> PARSER;

        private S2C() {
        }

        @Override
        public MsgCase getMsgCase() {
            return MsgCase.forNumber(this.msgCase_);
        }

        private void clearMsg() {
            this.msgCase_ = 0;
            this.msg_ = null;
        }

        @Override
        public boolean hasJoin() {
            return this.msgCase_ == 1;
        }

        @Override
        public UserJoin getJoin() {
            if (this.msgCase_ == 1) {
                return (UserJoin)this.msg_;
            }
            return UserJoin.getDefaultInstance();
        }

        private void setJoin(UserJoin value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 1;
        }

        private void mergeJoin(UserJoin value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 1 && this.msg_ != UserJoin.getDefaultInstance() ? ((UserJoin.Builder)UserJoin.newBuilder((UserJoin)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 1;
        }

        private void clearJoin() {
            if (this.msgCase_ == 1) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        @Override
        public boolean hasPart() {
            return this.msgCase_ == 2;
        }

        @Override
        public UserPart getPart() {
            if (this.msgCase_ == 2) {
                return (UserPart)this.msg_;
            }
            return UserPart.getDefaultInstance();
        }

        private void setPart(UserPart value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 2;
        }

        private void mergePart(UserPart value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 2 && this.msg_ != UserPart.getDefaultInstance() ? ((UserPart.Builder)UserPart.newBuilder((UserPart)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 2;
        }

        private void clearPart() {
            if (this.msgCase_ == 2) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        @Override
        public boolean hasData() {
            return this.msgCase_ == 3;
        }

        @Override
        public PartyData getData() {
            if (this.msgCase_ == 3) {
                return (PartyData)this.msg_;
            }
            return PartyData.getDefaultInstance();
        }

        private void setData(PartyData value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 3;
        }

        private void mergeData(PartyData value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 3 && this.msg_ != PartyData.getDefaultInstance() ? ((PartyData.Builder)PartyData.newBuilder((PartyData)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 3;
        }

        private void clearData() {
            if (this.msgCase_ == 3) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        public static S2C parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static S2C parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static S2C parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static S2C parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static S2C parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static S2C parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static S2C parseFrom(InputStream input) throws IOException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static S2C parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static S2C parseDelimitedFrom(InputStream input) throws IOException {
            return (S2C)S2C.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static S2C parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (S2C)S2C.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static S2C parseFrom(CodedInputStream input) throws IOException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static S2C parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (S2C)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(S2C prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new S2C();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"msg_", "msgCase_", UserJoin.class, UserPart.class, PartyData.class};
                    String info = "\u0000\u0003\u0001\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001<\u0000\u0002<\u0000\u0003<\u0000";
                    return S2C.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<S2C> clazz = S2C.class;
                    synchronized (S2C.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static S2C getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<S2C> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            S2C defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new S2C();
            GeneratedMessageLite.registerDefaultInstance(S2C.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<S2C, Builder>
        implements S2COrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public MsgCase getMsgCase() {
                return ((S2C)this.instance).getMsgCase();
            }

            public Builder clearMsg() {
                this.copyOnWrite();
                ((S2C)this.instance).clearMsg();
                return this;
            }

            @Override
            public boolean hasJoin() {
                return ((S2C)this.instance).hasJoin();
            }

            @Override
            public UserJoin getJoin() {
                return ((S2C)this.instance).getJoin();
            }

            public Builder setJoin(UserJoin value) {
                this.copyOnWrite();
                ((S2C)this.instance).setJoin(value);
                return this;
            }

            public Builder setJoin(UserJoin.Builder builderForValue) {
                this.copyOnWrite();
                ((S2C)this.instance).setJoin((UserJoin)builderForValue.build());
                return this;
            }

            public Builder mergeJoin(UserJoin value) {
                this.copyOnWrite();
                ((S2C)this.instance).mergeJoin(value);
                return this;
            }

            public Builder clearJoin() {
                this.copyOnWrite();
                ((S2C)this.instance).clearJoin();
                return this;
            }

            @Override
            public boolean hasPart() {
                return ((S2C)this.instance).hasPart();
            }

            @Override
            public UserPart getPart() {
                return ((S2C)this.instance).getPart();
            }

            public Builder setPart(UserPart value) {
                this.copyOnWrite();
                ((S2C)this.instance).setPart(value);
                return this;
            }

            public Builder setPart(UserPart.Builder builderForValue) {
                this.copyOnWrite();
                ((S2C)this.instance).setPart((UserPart)builderForValue.build());
                return this;
            }

            public Builder mergePart(UserPart value) {
                this.copyOnWrite();
                ((S2C)this.instance).mergePart(value);
                return this;
            }

            public Builder clearPart() {
                this.copyOnWrite();
                ((S2C)this.instance).clearPart();
                return this;
            }

            @Override
            public boolean hasData() {
                return ((S2C)this.instance).hasData();
            }

            @Override
            public PartyData getData() {
                return ((S2C)this.instance).getData();
            }

            public Builder setData(PartyData value) {
                this.copyOnWrite();
                ((S2C)this.instance).setData(value);
                return this;
            }

            public Builder setData(PartyData.Builder builderForValue) {
                this.copyOnWrite();
                ((S2C)this.instance).setData((PartyData)builderForValue.build());
                return this;
            }

            public Builder mergeData(PartyData value) {
                this.copyOnWrite();
                ((S2C)this.instance).mergeData(value);
                return this;
            }

            public Builder clearData() {
                this.copyOnWrite();
                ((S2C)this.instance).clearData();
                return this;
            }
        }

        public static enum MsgCase {
            JOIN(1),
            PART(2),
            DATA(3),
            MSG_NOT_SET(0);

            private final int value;

            private MsgCase(int value) {
                this.value = value;
            }

            @Deprecated
            public static MsgCase valueOf(int value) {
                return MsgCase.forNumber(value);
            }

            public static MsgCase forNumber(int value) {
                switch (value) {
                    case 1: {
                        return JOIN;
                    }
                    case 2: {
                        return PART;
                    }
                    case 3: {
                        return DATA;
                    }
                    case 0: {
                        return MSG_NOT_SET;
                    }
                }
                return null;
            }

            public int getNumber() {
                return this.value;
            }
        }
    }

    public static interface S2COrBuilder
    extends MessageLiteOrBuilder {
        public boolean hasJoin();

        public UserJoin getJoin();

        public boolean hasPart();

        public UserPart getPart();

        public boolean hasData();

        public PartyData getData();

        public S2C.MsgCase getMsgCase();
    }

    public static final class PartyData
    extends GeneratedMessageLite<PartyData, Builder>
    implements PartyDataOrBuilder {
        public static final int PARTYID_FIELD_NUMBER = 1;
        private long partyId_;
        public static final int MEMBERID_FIELD_NUMBER = 2;
        private long memberId_;
        public static final int TYPE_FIELD_NUMBER = 4;
        private String type_ = "";
        public static final int DATA_FIELD_NUMBER = 3;
        private ByteString data_ = ByteString.EMPTY;
        private static final PartyData DEFAULT_INSTANCE;
        private static volatile Parser<PartyData> PARSER;

        private PartyData() {
        }

        @Override
        public long getPartyId() {
            return this.partyId_;
        }

        private void setPartyId(long value) {
            this.partyId_ = value;
        }

        private void clearPartyId() {
            this.partyId_ = 0L;
        }

        @Override
        public long getMemberId() {
            return this.memberId_;
        }

        private void setMemberId(long value) {
            this.memberId_ = value;
        }

        private void clearMemberId() {
            this.memberId_ = 0L;
        }

        @Override
        public String getType() {
            return this.type_;
        }

        @Override
        public ByteString getTypeBytes() {
            return ByteString.copyFromUtf8((String)this.type_);
        }

        private void setType(String value) {
            Class<?> valueClass = value.getClass();
            this.type_ = value;
        }

        private void clearType() {
            this.type_ = PartyData.getDefaultInstance().getType();
        }

        private void setTypeBytes(ByteString value) {
            PartyData.checkByteStringIsUtf8((ByteString)value);
            this.type_ = value.toStringUtf8();
        }

        @Override
        public ByteString getData() {
            return this.data_;
        }

        private void setData(ByteString value) {
            Class<?> valueClass = value.getClass();
            this.data_ = value;
        }

        private void clearData() {
            this.data_ = PartyData.getDefaultInstance().getData();
        }

        public static PartyData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static PartyData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static PartyData parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static PartyData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static PartyData parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static PartyData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static PartyData parseFrom(InputStream input) throws IOException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static PartyData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static PartyData parseDelimitedFrom(InputStream input) throws IOException {
            return (PartyData)PartyData.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static PartyData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PartyData)PartyData.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static PartyData parseFrom(CodedInputStream input) throws IOException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static PartyData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PartyData)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(PartyData prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new PartyData();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"partyId_", "memberId_", "data_", "type_"};
                    String info = "\u0000\u0004\u0000\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001\u0002\u0002\u0002\u0003\n\u0004\u0208";
                    return PartyData.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<PartyData> clazz = PartyData.class;
                    synchronized (PartyData.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static PartyData getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PartyData> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            PartyData defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new PartyData();
            GeneratedMessageLite.registerDefaultInstance(PartyData.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<PartyData, Builder>
        implements PartyDataOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public long getPartyId() {
                return ((PartyData)this.instance).getPartyId();
            }

            public Builder setPartyId(long value) {
                this.copyOnWrite();
                ((PartyData)this.instance).setPartyId(value);
                return this;
            }

            public Builder clearPartyId() {
                this.copyOnWrite();
                ((PartyData)this.instance).clearPartyId();
                return this;
            }

            @Override
            public long getMemberId() {
                return ((PartyData)this.instance).getMemberId();
            }

            public Builder setMemberId(long value) {
                this.copyOnWrite();
                ((PartyData)this.instance).setMemberId(value);
                return this;
            }

            public Builder clearMemberId() {
                this.copyOnWrite();
                ((PartyData)this.instance).clearMemberId();
                return this;
            }

            @Override
            public String getType() {
                return ((PartyData)this.instance).getType();
            }

            @Override
            public ByteString getTypeBytes() {
                return ((PartyData)this.instance).getTypeBytes();
            }

            public Builder setType(String value) {
                this.copyOnWrite();
                ((PartyData)this.instance).setType(value);
                return this;
            }

            public Builder clearType() {
                this.copyOnWrite();
                ((PartyData)this.instance).clearType();
                return this;
            }

            public Builder setTypeBytes(ByteString value) {
                this.copyOnWrite();
                ((PartyData)this.instance).setTypeBytes(value);
                return this;
            }

            @Override
            public ByteString getData() {
                return ((PartyData)this.instance).getData();
            }

            public Builder setData(ByteString value) {
                this.copyOnWrite();
                ((PartyData)this.instance).setData(value);
                return this;
            }

            public Builder clearData() {
                this.copyOnWrite();
                ((PartyData)this.instance).clearData();
                return this;
            }
        }
    }

    public static interface PartyDataOrBuilder
    extends MessageLiteOrBuilder {
        public long getPartyId();

        public long getMemberId();

        public String getType();

        public ByteString getTypeBytes();

        public ByteString getData();
    }

    public static final class UserPart
    extends GeneratedMessageLite<UserPart, Builder>
    implements UserPartOrBuilder {
        public static final int PARTYID_FIELD_NUMBER = 1;
        private long partyId_;
        public static final int MEMBERID_FIELD_NUMBER = 2;
        private long memberId_;
        private static final UserPart DEFAULT_INSTANCE;
        private static volatile Parser<UserPart> PARSER;

        private UserPart() {
        }

        @Override
        public long getPartyId() {
            return this.partyId_;
        }

        private void setPartyId(long value) {
            this.partyId_ = value;
        }

        private void clearPartyId() {
            this.partyId_ = 0L;
        }

        @Override
        public long getMemberId() {
            return this.memberId_;
        }

        private void setMemberId(long value) {
            this.memberId_ = value;
        }

        private void clearMemberId() {
            this.memberId_ = 0L;
        }

        public static UserPart parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static UserPart parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserPart parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static UserPart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserPart parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static UserPart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserPart parseFrom(InputStream input) throws IOException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static UserPart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserPart parseDelimitedFrom(InputStream input) throws IOException {
            return (UserPart)UserPart.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static UserPart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserPart)UserPart.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserPart parseFrom(CodedInputStream input) throws IOException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static UserPart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserPart)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(UserPart prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new UserPart();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"partyId_", "memberId_"};
                    String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0002\u0002\u0002";
                    return UserPart.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<UserPart> clazz = UserPart.class;
                    synchronized (UserPart.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static UserPart getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<UserPart> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            UserPart defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new UserPart();
            GeneratedMessageLite.registerDefaultInstance(UserPart.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<UserPart, Builder>
        implements UserPartOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public long getPartyId() {
                return ((UserPart)this.instance).getPartyId();
            }

            public Builder setPartyId(long value) {
                this.copyOnWrite();
                ((UserPart)this.instance).setPartyId(value);
                return this;
            }

            public Builder clearPartyId() {
                this.copyOnWrite();
                ((UserPart)this.instance).clearPartyId();
                return this;
            }

            @Override
            public long getMemberId() {
                return ((UserPart)this.instance).getMemberId();
            }

            public Builder setMemberId(long value) {
                this.copyOnWrite();
                ((UserPart)this.instance).setMemberId(value);
                return this;
            }

            public Builder clearMemberId() {
                this.copyOnWrite();
                ((UserPart)this.instance).clearMemberId();
                return this;
            }
        }
    }

    public static interface UserPartOrBuilder
    extends MessageLiteOrBuilder {
        public long getPartyId();

        public long getMemberId();
    }

    public static final class UserJoin
    extends GeneratedMessageLite<UserJoin, Builder>
    implements UserJoinOrBuilder {
        public static final int PARTYID_FIELD_NUMBER = 1;
        private long partyId_;
        public static final int MEMBERID_FIELD_NUMBER = 2;
        private long memberId_;
        private static final UserJoin DEFAULT_INSTANCE;
        private static volatile Parser<UserJoin> PARSER;

        private UserJoin() {
        }

        @Override
        public long getPartyId() {
            return this.partyId_;
        }

        private void setPartyId(long value) {
            this.partyId_ = value;
        }

        private void clearPartyId() {
            this.partyId_ = 0L;
        }

        @Override
        public long getMemberId() {
            return this.memberId_;
        }

        private void setMemberId(long value) {
            this.memberId_ = value;
        }

        private void clearMemberId() {
            this.memberId_ = 0L;
        }

        public static UserJoin parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static UserJoin parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserJoin parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static UserJoin parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserJoin parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static UserJoin parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserJoin parseFrom(InputStream input) throws IOException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static UserJoin parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserJoin parseDelimitedFrom(InputStream input) throws IOException {
            return (UserJoin)UserJoin.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static UserJoin parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserJoin)UserJoin.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static UserJoin parseFrom(CodedInputStream input) throws IOException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static UserJoin parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (UserJoin)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(UserJoin prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new UserJoin();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"partyId_", "memberId_"};
                    String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0002\u0002\u0002";
                    return UserJoin.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<UserJoin> clazz = UserJoin.class;
                    synchronized (UserJoin.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static UserJoin getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<UserJoin> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            UserJoin defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new UserJoin();
            GeneratedMessageLite.registerDefaultInstance(UserJoin.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<UserJoin, Builder>
        implements UserJoinOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public long getPartyId() {
                return ((UserJoin)this.instance).getPartyId();
            }

            public Builder setPartyId(long value) {
                this.copyOnWrite();
                ((UserJoin)this.instance).setPartyId(value);
                return this;
            }

            public Builder clearPartyId() {
                this.copyOnWrite();
                ((UserJoin)this.instance).clearPartyId();
                return this;
            }

            @Override
            public long getMemberId() {
                return ((UserJoin)this.instance).getMemberId();
            }

            public Builder setMemberId(long value) {
                this.copyOnWrite();
                ((UserJoin)this.instance).setMemberId(value);
                return this;
            }

            public Builder clearMemberId() {
                this.copyOnWrite();
                ((UserJoin)this.instance).clearMemberId();
                return this;
            }
        }
    }

    public static interface UserJoinOrBuilder
    extends MessageLiteOrBuilder {
        public long getPartyId();

        public long getMemberId();
    }

    public static final class C2S
    extends GeneratedMessageLite<C2S, Builder>
    implements C2SOrBuilder {
        private int msgCase_ = 0;
        private Object msg_;
        public static final int JOIN_FIELD_NUMBER = 1;
        public static final int PART_FIELD_NUMBER = 2;
        public static final int DATA_FIELD_NUMBER = 3;
        private static final C2S DEFAULT_INSTANCE;
        private static volatile Parser<C2S> PARSER;

        private C2S() {
        }

        @Override
        public MsgCase getMsgCase() {
            return MsgCase.forNumber(this.msgCase_);
        }

        private void clearMsg() {
            this.msgCase_ = 0;
            this.msg_ = null;
        }

        @Override
        public boolean hasJoin() {
            return this.msgCase_ == 1;
        }

        @Override
        public Join getJoin() {
            if (this.msgCase_ == 1) {
                return (Join)this.msg_;
            }
            return Join.getDefaultInstance();
        }

        private void setJoin(Join value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 1;
        }

        private void mergeJoin(Join value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 1 && this.msg_ != Join.getDefaultInstance() ? ((Join.Builder)Join.newBuilder((Join)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 1;
        }

        private void clearJoin() {
            if (this.msgCase_ == 1) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        @Override
        public boolean hasPart() {
            return this.msgCase_ == 2;
        }

        @Override
        public Part getPart() {
            if (this.msgCase_ == 2) {
                return (Part)this.msg_;
            }
            return Part.getDefaultInstance();
        }

        private void setPart(Part value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 2;
        }

        private void mergePart(Part value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 2 && this.msg_ != Part.getDefaultInstance() ? ((Part.Builder)Part.newBuilder((Part)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 2;
        }

        private void clearPart() {
            if (this.msgCase_ == 2) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        @Override
        public boolean hasData() {
            return this.msgCase_ == 3;
        }

        @Override
        public Data getData() {
            if (this.msgCase_ == 3) {
                return (Data)this.msg_;
            }
            return Data.getDefaultInstance();
        }

        private void setData(Data value) {
            value.getClass();
            this.msg_ = value;
            this.msgCase_ = 3;
        }

        private void mergeData(Data value) {
            value.getClass();
            this.msg_ = this.msgCase_ == 3 && this.msg_ != Data.getDefaultInstance() ? ((Data.Builder)Data.newBuilder((Data)this.msg_).mergeFrom(value)).buildPartial() : value;
            this.msgCase_ = 3;
        }

        private void clearData() {
            if (this.msgCase_ == 3) {
                this.msgCase_ = 0;
                this.msg_ = null;
            }
        }

        public static C2S parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static C2S parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static C2S parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static C2S parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static C2S parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static C2S parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static C2S parseFrom(InputStream input) throws IOException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static C2S parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static C2S parseDelimitedFrom(InputStream input) throws IOException {
            return (C2S)C2S.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static C2S parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (C2S)C2S.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static C2S parseFrom(CodedInputStream input) throws IOException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static C2S parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (C2S)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(C2S prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new C2S();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"msg_", "msgCase_", Join.class, Part.class, Data.class};
                    String info = "\u0000\u0003\u0001\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001<\u0000\u0002<\u0000\u0003<\u0000";
                    return C2S.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<C2S> clazz = C2S.class;
                    synchronized (C2S.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static C2S getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<C2S> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            C2S defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new C2S();
            GeneratedMessageLite.registerDefaultInstance(C2S.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<C2S, Builder>
        implements C2SOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public MsgCase getMsgCase() {
                return ((C2S)this.instance).getMsgCase();
            }

            public Builder clearMsg() {
                this.copyOnWrite();
                ((C2S)this.instance).clearMsg();
                return this;
            }

            @Override
            public boolean hasJoin() {
                return ((C2S)this.instance).hasJoin();
            }

            @Override
            public Join getJoin() {
                return ((C2S)this.instance).getJoin();
            }

            public Builder setJoin(Join value) {
                this.copyOnWrite();
                ((C2S)this.instance).setJoin(value);
                return this;
            }

            public Builder setJoin(Join.Builder builderForValue) {
                this.copyOnWrite();
                ((C2S)this.instance).setJoin((Join)builderForValue.build());
                return this;
            }

            public Builder mergeJoin(Join value) {
                this.copyOnWrite();
                ((C2S)this.instance).mergeJoin(value);
                return this;
            }

            public Builder clearJoin() {
                this.copyOnWrite();
                ((C2S)this.instance).clearJoin();
                return this;
            }

            @Override
            public boolean hasPart() {
                return ((C2S)this.instance).hasPart();
            }

            @Override
            public Part getPart() {
                return ((C2S)this.instance).getPart();
            }

            public Builder setPart(Part value) {
                this.copyOnWrite();
                ((C2S)this.instance).setPart(value);
                return this;
            }

            public Builder setPart(Part.Builder builderForValue) {
                this.copyOnWrite();
                ((C2S)this.instance).setPart((Part)builderForValue.build());
                return this;
            }

            public Builder mergePart(Part value) {
                this.copyOnWrite();
                ((C2S)this.instance).mergePart(value);
                return this;
            }

            public Builder clearPart() {
                this.copyOnWrite();
                ((C2S)this.instance).clearPart();
                return this;
            }

            @Override
            public boolean hasData() {
                return ((C2S)this.instance).hasData();
            }

            @Override
            public Data getData() {
                return ((C2S)this.instance).getData();
            }

            public Builder setData(Data value) {
                this.copyOnWrite();
                ((C2S)this.instance).setData(value);
                return this;
            }

            public Builder setData(Data.Builder builderForValue) {
                this.copyOnWrite();
                ((C2S)this.instance).setData((Data)builderForValue.build());
                return this;
            }

            public Builder mergeData(Data value) {
                this.copyOnWrite();
                ((C2S)this.instance).mergeData(value);
                return this;
            }

            public Builder clearData() {
                this.copyOnWrite();
                ((C2S)this.instance).clearData();
                return this;
            }
        }

        public static enum MsgCase {
            JOIN(1),
            PART(2),
            DATA(3),
            MSG_NOT_SET(0);

            private final int value;

            private MsgCase(int value) {
                this.value = value;
            }

            @Deprecated
            public static MsgCase valueOf(int value) {
                return MsgCase.forNumber(value);
            }

            public static MsgCase forNumber(int value) {
                switch (value) {
                    case 1: {
                        return JOIN;
                    }
                    case 2: {
                        return PART;
                    }
                    case 3: {
                        return DATA;
                    }
                    case 0: {
                        return MSG_NOT_SET;
                    }
                }
                return null;
            }

            public int getNumber() {
                return this.value;
            }
        }
    }

    public static interface C2SOrBuilder
    extends MessageLiteOrBuilder {
        public boolean hasJoin();

        public Join getJoin();

        public boolean hasPart();

        public Part getPart();

        public boolean hasData();

        public Data getData();

        public C2S.MsgCase getMsgCase();
    }

    public static final class Data
    extends GeneratedMessageLite<Data, Builder>
    implements DataOrBuilder {
        public static final int TYPE_FIELD_NUMBER = 2;
        private String type_ = "";
        public static final int DATA_FIELD_NUMBER = 1;
        private ByteString data_ = ByteString.EMPTY;
        private static final Data DEFAULT_INSTANCE;
        private static volatile Parser<Data> PARSER;

        private Data() {
        }

        @Override
        public String getType() {
            return this.type_;
        }

        @Override
        public ByteString getTypeBytes() {
            return ByteString.copyFromUtf8((String)this.type_);
        }

        private void setType(String value) {
            Class<?> valueClass = value.getClass();
            this.type_ = value;
        }

        private void clearType() {
            this.type_ = Data.getDefaultInstance().getType();
        }

        private void setTypeBytes(ByteString value) {
            Data.checkByteStringIsUtf8((ByteString)value);
            this.type_ = value.toStringUtf8();
        }

        @Override
        public ByteString getData() {
            return this.data_;
        }

        private void setData(ByteString value) {
            Class<?> valueClass = value.getClass();
            this.data_ = value;
        }

        private void clearData() {
            this.data_ = Data.getDefaultInstance().getData();
        }

        public static Data parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static Data parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Data parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static Data parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Data parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static Data parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Data parseFrom(InputStream input) throws IOException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Data parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Data parseDelimitedFrom(InputStream input) throws IOException {
            return (Data)Data.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Data parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Data)Data.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Data parseFrom(CodedInputStream input) throws IOException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static Data parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Data)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Data prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new Data();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"data_", "type_"};
                    String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\n\u0002\u0208";
                    return Data.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<Data> clazz = Data.class;
                    synchronized (Data.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static Data getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Data> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            Data defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new Data();
            GeneratedMessageLite.registerDefaultInstance(Data.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<Data, Builder>
        implements DataOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public String getType() {
                return ((Data)this.instance).getType();
            }

            @Override
            public ByteString getTypeBytes() {
                return ((Data)this.instance).getTypeBytes();
            }

            public Builder setType(String value) {
                this.copyOnWrite();
                ((Data)this.instance).setType(value);
                return this;
            }

            public Builder clearType() {
                this.copyOnWrite();
                ((Data)this.instance).clearType();
                return this;
            }

            public Builder setTypeBytes(ByteString value) {
                this.copyOnWrite();
                ((Data)this.instance).setTypeBytes(value);
                return this;
            }

            @Override
            public ByteString getData() {
                return ((Data)this.instance).getData();
            }

            public Builder setData(ByteString value) {
                this.copyOnWrite();
                ((Data)this.instance).setData(value);
                return this;
            }

            public Builder clearData() {
                this.copyOnWrite();
                ((Data)this.instance).clearData();
                return this;
            }
        }
    }

    public static interface DataOrBuilder
    extends MessageLiteOrBuilder {
        public String getType();

        public ByteString getTypeBytes();

        public ByteString getData();
    }

    public static final class Part
    extends GeneratedMessageLite<Part, Builder>
    implements PartOrBuilder {
        private static final Part DEFAULT_INSTANCE;
        private static volatile Parser<Part> PARSER;

        private Part() {
        }

        public static Part parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static Part parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Part parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static Part parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Part parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static Part parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Part parseFrom(InputStream input) throws IOException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Part parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Part parseDelimitedFrom(InputStream input) throws IOException {
            return (Part)Part.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Part parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Part)Part.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Part parseFrom(CodedInputStream input) throws IOException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static Part parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Part)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Part prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new Part();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = null;
                    String info = "\u0000\u0000";
                    return Part.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<Part> clazz = Part.class;
                    synchronized (Part.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static Part getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Part> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            Part defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new Part();
            GeneratedMessageLite.registerDefaultInstance(Part.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<Part, Builder>
        implements PartOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }
        }
    }

    public static interface PartOrBuilder
    extends MessageLiteOrBuilder {
    }

    public static final class Join
    extends GeneratedMessageLite<Join, Builder>
    implements JoinOrBuilder {
        public static final int PARTYID_FIELD_NUMBER = 1;
        private long partyId_;
        public static final int MEMBERID_FIELD_NUMBER = 2;
        private long memberId_;
        private static final Join DEFAULT_INSTANCE;
        private static volatile Parser<Join> PARSER;

        private Join() {
        }

        @Override
        public long getPartyId() {
            return this.partyId_;
        }

        private void setPartyId(long value) {
            this.partyId_ = value;
        }

        private void clearPartyId() {
            this.partyId_ = 0L;
        }

        @Override
        public long getMemberId() {
            return this.memberId_;
        }

        private void setMemberId(long value) {
            this.memberId_ = value;
        }

        private void clearMemberId() {
            this.memberId_ = 0L;
        }

        public static Join parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data);
        }

        public static Join parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteBuffer)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Join parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data);
        }

        public static Join parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (ByteString)data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Join parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data);
        }

        public static Join parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (byte[])data, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Join parseFrom(InputStream input) throws IOException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Join parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Join parseDelimitedFrom(InputStream input) throws IOException {
            return (Join)Join.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input);
        }

        public static Join parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Join)Join.parseDelimitedFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (InputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Join parseFrom(CodedInputStream input) throws IOException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input);
        }

        public static Join parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Join)GeneratedMessageLite.parseFrom((GeneratedMessageLite)DEFAULT_INSTANCE, (CodedInputStream)input, (ExtensionRegistryLite)extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Join prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE: {
                    return new Join();
                }
                case NEW_BUILDER: {
                    return new Builder();
                }
                case BUILD_MESSAGE_INFO: {
                    Object[] objects = new Object[]{"partyId_", "memberId_"};
                    String info = "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0002\u0002\u0002";
                    return Join.newMessageInfo((MessageLite)DEFAULT_INSTANCE, (String)info, (Object[])objects);
                }
                case GET_DEFAULT_INSTANCE: {
                    return DEFAULT_INSTANCE;
                }
                case GET_PARSER: {
                    GeneratedMessageLite.DefaultInstanceBasedParser parser = PARSER;
                    if (parser != null) return parser;
                    Class<Join> clazz = Join.class;
                    synchronized (Join.class) {
                        parser = PARSER;
                        if (parser != null) return parser;
                        PARSER = parser = new GeneratedMessageLite.DefaultInstanceBasedParser((GeneratedMessageLite)DEFAULT_INSTANCE);
                        // ** MonitorExit[var5_7] (shouldn't be in output)
                        return parser;
                    }
                }
                case GET_MEMOIZED_IS_INITIALIZED: {
                    return (byte)1;
                }
                case SET_MEMOIZED_IS_INITIALIZED: {
                    return null;
                }
            }
            throw new UnsupportedOperationException();
        }

        public static Join getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Join> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            Join defaultInstance;
            DEFAULT_INSTANCE = defaultInstance = new Join();
            GeneratedMessageLite.registerDefaultInstance(Join.class, (GeneratedMessageLite)defaultInstance);
        }

        public static final class Builder
        extends GeneratedMessageLite.Builder<Join, Builder>
        implements JoinOrBuilder {
            private Builder() {
                super((GeneratedMessageLite)DEFAULT_INSTANCE);
            }

            @Override
            public long getPartyId() {
                return ((Join)this.instance).getPartyId();
            }

            public Builder setPartyId(long value) {
                this.copyOnWrite();
                ((Join)this.instance).setPartyId(value);
                return this;
            }

            public Builder clearPartyId() {
                this.copyOnWrite();
                ((Join)this.instance).clearPartyId();
                return this;
            }

            @Override
            public long getMemberId() {
                return ((Join)this.instance).getMemberId();
            }

            public Builder setMemberId(long value) {
                this.copyOnWrite();
                ((Join)this.instance).setMemberId(value);
                return this;
            }

            public Builder clearMemberId() {
                this.copyOnWrite();
                ((Join)this.instance).clearMemberId();
                return this;
            }
        }
    }

    public static interface JoinOrBuilder
    extends MessageLiteOrBuilder {
        public long getPartyId();

        public long getMemberId();
    }
}

