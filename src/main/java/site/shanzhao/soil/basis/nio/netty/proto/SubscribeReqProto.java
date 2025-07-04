// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SubscribeReq.proto

package site.shanzhao.soil.basis.nio.netty.proto;

public final class SubscribeReqProto {
  private SubscribeReqProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface SubscribeReqOrBuilder extends
      // @@protoc_insertion_point(interface_extends:netty.SubscribeReq)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 subReqId = 1;</code>
     * @return Whether the subReqId field is set.
     */
    boolean hasSubReqId();
    /**
     * <code>required int32 subReqId = 1;</code>
     * @return The subReqId.
     */
    int getSubReqId();

    /**
     * <code>required string username = 2;</code>
     * @return Whether the username field is set.
     */
    boolean hasUsername();
    /**
     * <code>required string username = 2;</code>
     * @return The username.
     */
    String getUsername();
    /**
     * <code>required string username = 2;</code>
     * @return The bytes for username.
     */
    com.google.protobuf.ByteString
        getUsernameBytes();

    /**
     * <code>required string productName = 3;</code>
     * @return Whether the productName field is set.
     */
    boolean hasProductName();
    /**
     * <code>required string productName = 3;</code>
     * @return The productName.
     */
    String getProductName();
    /**
     * <code>required string productName = 3;</code>
     * @return The bytes for productName.
     */
    com.google.protobuf.ByteString
        getProductNameBytes();

    /**
     * <code>repeated string address = 4;</code>
     * @return A list containing the address.
     */
    java.util.List<String>
        getAddressList();
    /**
     * <code>repeated string address = 4;</code>
     * @return The count of address.
     */
    int getAddressCount();
    /**
     * <code>repeated string address = 4;</code>
     * @param index The index of the element to return.
     * @return The address at the given index.
     */
    String getAddress(int index);
    /**
     * <code>repeated string address = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the address at the given index.
     */
    com.google.protobuf.ByteString
        getAddressBytes(int index);
  }
  /**
   * Protobuf type {@code netty.SubscribeReq}
   */
  public static final class SubscribeReq extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:netty.SubscribeReq)
      SubscribeReqOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SubscribeReq.newBuilder() to construct.
    private SubscribeReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SubscribeReq() {
      username_ = "";
      productName_ = "";
      address_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new SubscribeReq();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SubscribeReq(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              bitField0_ |= 0x00000001;
              subReqId_ = input.readInt32();
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              username_ = bs;
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              productName_ = bs;
              break;
            }
            case 34: {
              com.google.protobuf.ByteString bs = input.readBytes();
              if (!((mutable_bitField0_ & 0x00000008) != 0)) {
                address_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000008;
              }
              address_.add(bs);
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000008) != 0)) {
          address_ = address_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return SubscribeReqProto.internal_static_netty_SubscribeReq_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return SubscribeReqProto.internal_static_netty_SubscribeReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              SubscribeReq.class, Builder.class);
    }

    private int bitField0_;
    public static final int SUBREQID_FIELD_NUMBER = 1;
    private int subReqId_;
    /**
     * <code>required int32 subReqId = 1;</code>
     * @return Whether the subReqId field is set.
     */
    @Override
    public boolean hasSubReqId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required int32 subReqId = 1;</code>
     * @return The subReqId.
     */
    @Override
    public int getSubReqId() {
      return subReqId_;
    }

    public static final int USERNAME_FIELD_NUMBER = 2;
    private volatile Object username_;
    /**
     * <code>required string username = 2;</code>
     * @return Whether the username field is set.
     */
    @Override
    public boolean hasUsername() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required string username = 2;</code>
     * @return The username.
     */
    @Override
    public String getUsername() {
      Object ref = username_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          username_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string username = 2;</code>
     * @return The bytes for username.
     */
    @Override
    public com.google.protobuf.ByteString
        getUsernameBytes() {
      Object ref = username_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        username_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int PRODUCTNAME_FIELD_NUMBER = 3;
    private volatile Object productName_;
    /**
     * <code>required string productName = 3;</code>
     * @return Whether the productName field is set.
     */
    @Override
    public boolean hasProductName() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>required string productName = 3;</code>
     * @return The productName.
     */
    @Override
    public String getProductName() {
      Object ref = productName_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          productName_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string productName = 3;</code>
     * @return The bytes for productName.
     */
    @Override
    public com.google.protobuf.ByteString
        getProductNameBytes() {
      Object ref = productName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        productName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ADDRESS_FIELD_NUMBER = 4;
    private com.google.protobuf.LazyStringList address_;
    /**
     * <code>repeated string address = 4;</code>
     * @return A list containing the address.
     */
    public com.google.protobuf.ProtocolStringList
        getAddressList() {
      return address_;
    }
    /**
     * <code>repeated string address = 4;</code>
     * @return The count of address.
     */
    public int getAddressCount() {
      return address_.size();
    }
    /**
     * <code>repeated string address = 4;</code>
     * @param index The index of the element to return.
     * @return The address at the given index.
     */
    public String getAddress(int index) {
      return address_.get(index);
    }
    /**
     * <code>repeated string address = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the address at the given index.
     */
    public com.google.protobuf.ByteString
        getAddressBytes(int index) {
      return address_.getByteString(index);
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasSubReqId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasUsername()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasProductName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeInt32(1, subReqId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, username_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, productName_);
      }
      for (int i = 0; i < address_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, address_.getRaw(i));
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, subReqId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, username_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, productName_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < address_.size(); i++) {
          dataSize += computeStringSizeNoTag(address_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getAddressList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof SubscribeReq)) {
        return super.equals(obj);
      }
      SubscribeReq other = (SubscribeReq) obj;

      if (hasSubReqId() != other.hasSubReqId()) return false;
      if (hasSubReqId()) {
        if (getSubReqId()
            != other.getSubReqId()) return false;
      }
      if (hasUsername() != other.hasUsername()) return false;
      if (hasUsername()) {
        if (!getUsername()
            .equals(other.getUsername())) return false;
      }
      if (hasProductName() != other.hasProductName()) return false;
      if (hasProductName()) {
        if (!getProductName()
            .equals(other.getProductName())) return false;
      }
      if (!getAddressList()
          .equals(other.getAddressList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSubReqId()) {
        hash = (37 * hash) + SUBREQID_FIELD_NUMBER;
        hash = (53 * hash) + getSubReqId();
      }
      if (hasUsername()) {
        hash = (37 * hash) + USERNAME_FIELD_NUMBER;
        hash = (53 * hash) + getUsername().hashCode();
      }
      if (hasProductName()) {
        hash = (37 * hash) + PRODUCTNAME_FIELD_NUMBER;
        hash = (53 * hash) + getProductName().hashCode();
      }
      if (getAddressCount() > 0) {
        hash = (37 * hash) + ADDRESS_FIELD_NUMBER;
        hash = (53 * hash) + getAddressList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static SubscribeReq parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static SubscribeReq parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static SubscribeReq parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static SubscribeReq parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static SubscribeReq parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static SubscribeReq parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static SubscribeReq parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static SubscribeReq parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static SubscribeReq parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static SubscribeReq parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static SubscribeReq parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static SubscribeReq parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(SubscribeReq prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code netty.SubscribeReq}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:netty.SubscribeReq)
        SubscribeReqOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return SubscribeReqProto.internal_static_netty_SubscribeReq_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return SubscribeReqProto.internal_static_netty_SubscribeReq_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                SubscribeReq.class, Builder.class);
      }

      // Construct using com.github.soil.basis.nio.netty.proto.SubscribeReqProto.SubscribeReq.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        subReqId_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        username_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        productName_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        address_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return SubscribeReqProto.internal_static_netty_SubscribeReq_descriptor;
      }

      @Override
      public SubscribeReq getDefaultInstanceForType() {
        return SubscribeReq.getDefaultInstance();
      }

      @Override
      public SubscribeReq build() {
        SubscribeReq result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public SubscribeReq buildPartial() {
        SubscribeReq result = new SubscribeReq(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.subReqId_ = subReqId_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.username_ = username_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.productName_ = productName_;
        if (((bitField0_ & 0x00000008) != 0)) {
          address_ = address_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000008);
        }
        result.address_ = address_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof SubscribeReq) {
          return mergeFrom((SubscribeReq)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(SubscribeReq other) {
        if (other == SubscribeReq.getDefaultInstance()) return this;
        if (other.hasSubReqId()) {
          setSubReqId(other.getSubReqId());
        }
        if (other.hasUsername()) {
          bitField0_ |= 0x00000002;
          username_ = other.username_;
          onChanged();
        }
        if (other.hasProductName()) {
          bitField0_ |= 0x00000004;
          productName_ = other.productName_;
          onChanged();
        }
        if (!other.address_.isEmpty()) {
          if (address_.isEmpty()) {
            address_ = other.address_;
            bitField0_ = (bitField0_ & ~0x00000008);
          } else {
            ensureAddressIsMutable();
            address_.addAll(other.address_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        if (!hasSubReqId()) {
          return false;
        }
        if (!hasUsername()) {
          return false;
        }
        if (!hasProductName()) {
          return false;
        }
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        SubscribeReq parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (SubscribeReq) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int subReqId_ ;
      /**
       * <code>required int32 subReqId = 1;</code>
       * @return Whether the subReqId field is set.
       */
      @Override
      public boolean hasSubReqId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>required int32 subReqId = 1;</code>
       * @return The subReqId.
       */
      @Override
      public int getSubReqId() {
        return subReqId_;
      }
      /**
       * <code>required int32 subReqId = 1;</code>
       * @param value The subReqId to set.
       * @return This builder for chaining.
       */
      public Builder setSubReqId(int value) {
        bitField0_ |= 0x00000001;
        subReqId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 subReqId = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearSubReqId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        subReqId_ = 0;
        onChanged();
        return this;
      }

      private Object username_ = "";
      /**
       * <code>required string username = 2;</code>
       * @return Whether the username field is set.
       */
      public boolean hasUsername() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>required string username = 2;</code>
       * @return The username.
       */
      public String getUsername() {
        Object ref = username_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            username_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string username = 2;</code>
       * @return The bytes for username.
       */
      public com.google.protobuf.ByteString
          getUsernameBytes() {
        Object ref = username_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          username_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string username = 2;</code>
       * @param value The username to set.
       * @return This builder for chaining.
       */
      public Builder setUsername(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        username_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string username = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearUsername() {
        bitField0_ = (bitField0_ & ~0x00000002);
        username_ = getDefaultInstance().getUsername();
        onChanged();
        return this;
      }
      /**
       * <code>required string username = 2;</code>
       * @param value The bytes for username to set.
       * @return This builder for chaining.
       */
      public Builder setUsernameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        username_ = value;
        onChanged();
        return this;
      }

      private Object productName_ = "";
      /**
       * <code>required string productName = 3;</code>
       * @return Whether the productName field is set.
       */
      public boolean hasProductName() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>required string productName = 3;</code>
       * @return The productName.
       */
      public String getProductName() {
        Object ref = productName_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            productName_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string productName = 3;</code>
       * @return The bytes for productName.
       */
      public com.google.protobuf.ByteString
          getProductNameBytes() {
        Object ref = productName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          productName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string productName = 3;</code>
       * @param value The productName to set.
       * @return This builder for chaining.
       */
      public Builder setProductName(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        productName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string productName = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearProductName() {
        bitField0_ = (bitField0_ & ~0x00000004);
        productName_ = getDefaultInstance().getProductName();
        onChanged();
        return this;
      }
      /**
       * <code>required string productName = 3;</code>
       * @param value The bytes for productName to set.
       * @return This builder for chaining.
       */
      public Builder setProductNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        productName_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList address_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureAddressIsMutable() {
        if (!((bitField0_ & 0x00000008) != 0)) {
          address_ = new com.google.protobuf.LazyStringArrayList(address_);
          bitField0_ |= 0x00000008;
         }
      }
      /**
       * <code>repeated string address = 4;</code>
       * @return A list containing the address.
       */
      public com.google.protobuf.ProtocolStringList
          getAddressList() {
        return address_.getUnmodifiableView();
      }
      /**
       * <code>repeated string address = 4;</code>
       * @return The count of address.
       */
      public int getAddressCount() {
        return address_.size();
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param index The index of the element to return.
       * @return The address at the given index.
       */
      public String getAddress(int index) {
        return address_.get(index);
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param index The index of the value to return.
       * @return The bytes of the address at the given index.
       */
      public com.google.protobuf.ByteString
          getAddressBytes(int index) {
        return address_.getByteString(index);
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param index The index to set the value at.
       * @param value The address to set.
       * @return This builder for chaining.
       */
      public Builder setAddress(
          int index, String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAddressIsMutable();
        address_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param value The address to add.
       * @return This builder for chaining.
       */
      public Builder addAddress(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAddressIsMutable();
        address_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param values The address to add.
       * @return This builder for chaining.
       */
      public Builder addAllAddress(
          Iterable<String> values) {
        ensureAddressIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, address_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string address = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearAddress() {
        address_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000008);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string address = 4;</code>
       * @param value The bytes of the address to add.
       * @return This builder for chaining.
       */
      public Builder addAddressBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAddressIsMutable();
        address_.add(value);
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:netty.SubscribeReq)
    }

    // @@protoc_insertion_point(class_scope:netty.SubscribeReq)
    private static final SubscribeReq DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new SubscribeReq();
    }

    public static SubscribeReq getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<SubscribeReq>
        PARSER = new com.google.protobuf.AbstractParser<SubscribeReq>() {
      @Override
      public SubscribeReq parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SubscribeReq(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SubscribeReq> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<SubscribeReq> getParserForType() {
      return PARSER;
    }

    @Override
    public SubscribeReq getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_netty_SubscribeReq_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_netty_SubscribeReq_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\022SubscribeReq.proto\022\005netty\"X\n\014Subscribe" +
      "Req\022\020\n\010subReqId\030\001 \002(\005\022\020\n\010username\030\002 \002(\t\022" +
      "\023\n\013productName\030\003 \002(\t\022\017\n\007address\030\004 \003(\tB:\n" +
      "%com.github.soil.basis.nio.netty.protoB\021" +
      "SubscribeReqProto"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_netty_SubscribeReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_netty_SubscribeReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_netty_SubscribeReq_descriptor,
        new String[] { "SubReqId", "Username", "ProductName", "Address", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
