/*
 * Cerberus-Data is a complex data management library
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek;

import com.cerberustek.data.*;
import com.cerberustek.data.impl.DiscriminatorMapImpl;
import com.cerberustek.data.impl.elements.*;
import com.cerberustek.data.impl.streams.MetaInputStreamImpl;
import com.cerberustek.data.impl.streams.MetaOutputStreamImpl;
import com.cerberustek.data.impl.tags.*;
import com.cerberustek.querry.trace.impl.pull.*;
import com.cerberustek.querry.trace.impl.remove.*;
import com.cerberustek.querry.trace.impl.replace.*;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.logic.math.Vector2i;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.ResourceLocation;
import com.cerberustek.querry.trace.QueryTrace;
import com.cerberustek.querry.trace.TraceBuilder;
import com.cerberustek.querry.trace.impl.SimpleTraceBuilder;
import com.cerberustek.querry.trace.impl.SuccessResult;
import com.cerberustek.querry.trace.impl.SuccessResultBuilder;
import com.cerberustek.querry.trace.impl.append.ApTraceMap;
import com.cerberustek.querry.trace.impl.append.ApTraceMapBuilder;
import com.cerberustek.querry.trace.impl.append.ApTraceResource;
import com.cerberustek.querry.trace.impl.append.ApTraceResourceBuilder;
import com.cerberustek.querry.trace.impl.insert.InsTraceIndex;
import com.cerberustek.querry.trace.impl.insert.InsTraceIndexBuilder;
import com.cerberustek.service.CerberusService;
import com.cerberustek.utils.DiscriminatorFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.logging.Logger;

public class CerberusData implements CerberusService {

    public static final int DISCRIMINATOR_SIZE = 2;
    public static final int SIZE_DEC = 8;
    public static final short CERBERUS_NULL = 0;

    private static Logger logger = Logger.getLogger("Cerberus-Data");

    public static long size(MetaData data) {
        return data.byteSize();
    }

    public static long totalSize(MetaData data) {
        long size = DISCRIMINATOR_SIZE;
        if (data instanceof MetaTag)
            size += totalSize(((MetaTag) data).getTag());
        if (data.finalSize() < 0)
            size += SIZE_DEC;
        return size + data.byteSize();
    }

    public static long totalSize(String value) {
        return 2 + value.getBytes().length;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        CerberusData.logger = logger;
    }

    public static TraceBuilder genBuilder() {
        return new SimpleTraceBuilder();
    }

    public static QueryTrace parseTrace(String path) {
        if (path.startsWith("."))
            path = path.substring(1);

        TraceBuilder builder = new SimpleTraceBuilder();
        String[] s = path.split(".");

        if (s.length > 1) {
            for (String i : s)
                builder.stack(parseTrace(i));
        } else {

            if (path.length() > 4) {
                String end = path.substring(path.length() - 4);

                if (end.startsWith("[") && end.endsWith("]")) {
                    builder.stackTagLoc(end.substring(0, end.length() - 4));
                    builder.stackIndexLoc(Integer.parseInt(end.substring(1, end.length() - 2)));
                    return builder.build();
                }
            }
            builder.stackTagLoc(path);
        }
        return builder.build();
    }

    public static MetaInputStream createInputStream(InputStream inputStream, DiscriminatorMap discriminatorMap) {
        return new MetaInputStreamImpl(inputStream, discriminatorMap);
    }

    public static MetaOutputStream createOutputStream(OutputStream outputStream, DiscriminatorMap discriminatorMap) {
        return new MetaOutputStreamImpl(outputStream, discriminatorMap);
    }

    public static MetaInputStream createInputStream(InputStream inputStream) {
        return createInputStream(inputStream, genDefaultDiscriminators());
    }

    public static MetaOutputStream createOutputStream(OutputStream outputStream) {
        return createOutputStream(outputStream, genDefaultDiscriminators());
    }

    public static DiscriminatorMap genEmptyDiscriminators() {
        return new DiscriminatorMapImpl();
    }

    public static DiscriminatorMap genDefaultDiscriminators() {
        DiscriminatorMap map = new DiscriminatorMapImpl();
        map.registerData(ByteElement.class, new ByteElementBuilder(), (short) 1);
        map.registerData(ShortElement.class, new ShortElementBuilder(), (short) 2);
        map.registerData(CharElement.class, new CharElementBuilder(), (short) 3);
        map.registerData(IntElement.class, new IntElementBuilder(), (short) 4);
        map.registerData(LongElement.class, new LongElementBuilder(), (short) 5);
        map.registerData(FloatElement.class, new FloatElementBuilder(), (short) 6);
        map.registerData(DoubleElement.class, new DoubleElementBuilder(), (short) 7);
        map.registerData(StringElement.class, new StringElementBuilder(), (short) 8);
        map.registerData(Vector2iElement.class, new Vector2iElementBuilder(), (short) 9);
        map.registerData(Vector2lElement.class, new Vector2lElementBuilder(), (short) 10);
        map.registerData(Vector2fElement.class, new Vector2fElementBuilder(), (short) 11);
        map.registerData(Vector2dElement.class, new Vector2dElementBuilder(), (short) 12);
        map.registerData(Vector3iElement.class, new Vector3iElementBuilder(), (short) 13);
        map.registerData(Vector3lElement.class, new Vector3lElementBuilder(), (short) 14);
        map.registerData(Vector3fElement.class, new Vector3fElementBuilder(), (short) 15);
        map.registerData(Vector3dElement.class, new Vector3dElementBuilder(), (short) 16);
        map.registerData(QuaternionfElement.class, new QuaternionfElementBuilder(),  (short) 17);
        map.registerData(QuaterniondElement.class, new QuaterniondElementBuilder(), (short) 18);
        map.registerData(Matrix4fElement.class, new Matrix4fElementBuilder(), (short) 19);
        map.registerData(ArrayElement.class, new ArrayElementBuilder(), (short) 20);
        map.registerData(ListElement.class, new ListElementBuilder(), (short) 21);
        map.registerData(SpecificArrayElement.class, new SpecificArrayElementBuilder(), (short) 22);
        map.registerData(SetElement.class, new SetElementBuilder(), (short) 23);
        map.registerData(MapElement.class, new MapElementBuilder(), (short) 24);
        map.registerData(DocElement.class, new DocElementBuilder(), (short) 25);
        map.registerData(CompressionElement.class, new CompressionElementBuilder(), (short) 26);
        map.registerData(ContainerElement.class, new ContainerElementBuilder(), (short) 27);
        map.registerData(BooleanElement.class, new BooleanElementBuilder(), (short) 28);
        map.registerData(UUIDElement.class, new UUIDElementBuilder(), (short) 29);
        map.registerData(ExternalURIElement.class, new ExternalURIElementBuilder(), (short) 30);
        map.registerData(ExternalFileElement.class, new ExternalFileElementBuilder(), (short) 31);
        map.registerData(IVElement.class, new IVElementBuilder(), (short) 32);
        map.registerData(PrivateKeyElement.class, new PrivateKeyElementBuilder(), (short) 33);
        map.registerData(PublicKeyElement.class, new PublicKeyElementBuilder(), (short) 34);
        map.registerData(SecretKeyElement.class, new SecretKeyElementBuilder(), (short) 35);
        map.registerData(Vector4iElement.class, new Vector4iElementBuilder(), (short) 36);
        map.registerData(Vector4fElement.class, new Vector4fElementBuilder(), (short) 37);
        map.registerData(Vector4lElement.class, new Vector4lElementBuilder(), (short) 38);
        map.registerData(Vector4dElement.class, new Vector4dElementBuilder(), (short) 39);
        map.registerData(ClassElement.class, new ClassElementBuilder(), (short) 40);
        map.registerData(EncryptionElement.class, new EncryptionElementBuilder(), (short) 41);

        map.registerData(ByteTag.class, new ByteTagBuilder(), (short) 50);
        map.registerData(ShortTag.class, new ShortTagBuilder(), (short) 51);
        map.registerData(CharTag.class, new CharTagBuilder(), (short) 52);
        map.registerData(IntTag.class, new IntTagBuilder(), (short) 53);
        map.registerData(LongTag.class, new LongTagBuilder(), (short) 54);
        map.registerData(FloatTag.class, new FloatTagBuilder(), (short) 55);
        map.registerData(DoubleTag.class, new DoubleTagBuilder(), (short) 56);
        map.registerData(StringTag.class, new StringTagBuilder(), (short) 57);
        map.registerData(Vector2iTag.class, new Vector2iTagBuilder(), (short) 58);
        map.registerData(Vector2lTag.class, new Vector2lTagBuilder(), (short) 59);
        map.registerData(Vector2fTag.class, new Vector2fTagBuilder(), (short) 60);
        map.registerData(Vector2dTag.class, new Vector2dTagBuilder(), (short) 61);
        map.registerData(Vector3iTag.class, new Vector3iTagBuilder(), (short) 62);
        map.registerData(Vector3lTag.class, new Vector3lTagBuilder(), (short) 63);
        map.registerData(Vector3fTag.class, new Vector3fTagBuilder(), (short) 64);
        map.registerData(Vector3dTag.class, new Vector3dTagBuilder(), (short) 65);
        map.registerData(QuaternionfTag.class, new QuaternionfTagBuilder(), (short) 66);
        map.registerData(QuaterniondTag.class, new QuaterniondTagBuilder(), (short) 67);
        map.registerData(Matrix4fTag.class, new Matrix4fTagBuilder(), (short) 68);
        map.registerData(ArrayTag.class, new ArrayTagBuilder(), (short) 69);
        map.registerData(ListTag.class, new ListTagBuilder(), (short) 70);
        map.registerData(SpecificArrayTag.class, new SpecificArrayTagBuilder(), (short) 71);
        map.registerData(SetTag.class, new SetTagBuilder(), (short) 72);
        map.registerData(MapTag.class, new MapTagBuilder(), (short) 73);
        map.registerData(DocTag.class, new DocTagBuilder(), (short) 74);
        map.registerData(CompressionTag.class, new CompressionTagBuilder(), (short) 75);
        map.registerData(ContainerTag.class, new ContainerTagBuilder(), (short) 76);
        map.registerData(BooleanTag.class, new BooleanTagBuilder(), (short) 77);
        map.registerData(UUIDTag.class, new UUIDTagBuilder(), (short) 78);
        map.registerData(ExternalURITag.class, new ExternalURITagBuilder(), (short) 79);
        map.registerData(ExternalFileTag.class, new ExternalFileTagBuilder(), (short) 80);
        map.registerData(IVTag.class, new IVTagBuilder(), (short) 81);
        map.registerData(PrivateKeyTag.class, new PrivateKeyTagBuilder(), (short) 82);
        map.registerData(PublicKeyTag.class, new PublicKeyTagBuilder(), (short) 83);
        map.registerData(SecretKeyTag.class, new SecretKeyTagBuilder(), (short) 84);
        map.registerData(Vector4iTag.class, new Vector4iTagBuilder(), (short) 85);
        map.registerData(Vector4fTag.class, new Vector4fTagBuilder(), (short) 86);
        map.registerData(Vector4lTag.class, new Vector4lTagBuilder(), (short) 87);
        map.registerData(Vector4dTag.class, new Vector4dTagBuilder(), (short) 88);
        map.registerData(ClassTag.class, new ClassTagBuilder(), (short) 89);
        map.registerData(EncryptionTag.class, new EncryptionTagBuilder(), (short) 90);

        map.registerData(ReplTraceTag.class, new ReplTraceTagBuilder(), (short) 100);
        map.registerData(ReplTraceIndex.class, new ReplTraceIndexBuilder(), (short) 101);
        map.registerData(ReplTraceElement.class, new ReplTraceElementBuilder(), (short) 102);
        map.registerData(RemTraceTag.class, new RemTraceTagBuilder(), (short) 103);
        map.registerData(RemTraceIndex.class, new RemTraceIndexBuilder(), (short) 104);
        map.registerData(RemTraceElement.class, new RemTraceElementBuilder(), (short) 105);
        map.registerData(PullTraceTag.class, new PullTraceTagBuilder(), (short) 106);
        map.registerData(PullTraceIndex.class, new PullTraceIndexBuilder(), (short) 107);
        map.registerData(PullTraceElement.class, new PullTraceElementBuilder(), (short) 108);
        map.registerData(InsTraceIndex.class, new InsTraceIndexBuilder(), (short) 109);
        map.registerData(ApTraceResource.class, new ApTraceResourceBuilder(), (short) 110);
        map.registerData(ApTraceMap.class, new ApTraceMapBuilder(), (short) 111);
        map.registerData(SuccessResult.class, new SuccessResultBuilder(), (short) 112);
        map.registerData(PullResult.class, new PullResultBuilder(), (short) 113);

        return map;
    }

    public static DiscriminatorMap readDiscriminatorMap(String discriminatorFile) {
        File file = new File(discriminatorFile);
        DiscriminatorFile f = new DiscriminatorFile(file);
        DiscriminatorMap map;

        if (file.exists()) {
            try {
                map = f.read();
            } catch (FileNotFoundException e) {
                CerberusRegistry.getInstance().fine("Unable to read discriminators from existing file. Generating default");
                map = genDefaultDiscriminators();
                try {
                    f.write(map);
                } catch (IOException | UnknownDiscriminatorException ex) {
                    CerberusRegistry.getInstance().warning("Unable to write discriminators to file: " + ex);
                }
            }
        } else {
            CerberusRegistry.getInstance().info("Generating discriminator file");
            map = genDefaultDiscriminators();
            try {
                f.write(map);
            } catch (UnknownDiscriminatorException | IOException e) {
                CerberusRegistry.getInstance().warning("Unable to write discriminators to file: " + e);
            }
        }
        return map;
    }

    public static DiscriminatorMap readDiscriminatorMap(String discriminatorFile, DiscriminatorMap defaultMap) {
        File file = new File(discriminatorFile);
        DiscriminatorFile f = new DiscriminatorFile(file);
        DiscriminatorMap map;

        if (file.exists()) {
            try {
                map = f.read();
            } catch (FileNotFoundException e) {
                CerberusRegistry.getInstance().fine("Unable to read discriminators from existing file. Generating default");
                map = defaultMap;
                try {
                    f.write(map);
                } catch (IOException | UnknownDiscriminatorException ex) {
                    CerberusRegistry.getInstance().warning("Unable to write discriminators to file: " + ex);
                }
            }
        } else {
            CerberusRegistry.getInstance().info("Generating discriminator file");
            map = defaultMap;
            try {
                f.write(map);
            } catch (UnknownDiscriminatorException | IOException e) {
                CerberusRegistry.getInstance().warning("Unable to write discriminators to file: " + e);
            }
        }
        return map;
    }

    public static boolean writeDiscriminatorMap(String discriminatorFile, DiscriminatorMap map) {
        File file = new File(discriminatorFile);
        DiscriminatorFile f = new DiscriminatorFile(file);

        try {
            f.write(map);
            return true;
        } catch (IOException | UnknownDiscriminatorException e) {
            CerberusRegistry.getInstance().warning("Failed to write discriminator file: " + e);
            return false;
        }
    }

    public static ContainerTag serializeBufferedImageARGB(String tag, BufferedImage image) {
        byte[] data = new byte[image.getWidth() * image.getHeight() * 4];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int[] argb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), argb, 0, image.getWidth());

        buffer.rewind();
        for (int pixel : argb)
            buffer.putInt(pixel);

        return new ContainerTag(tag, data);
    }

    public static BufferedImage deserializeBufferedImageARGB(ContainerElement element, Vector2i size) {
        BufferedImage image = new BufferedImage(size.getX(), size.getY(), BufferedImage.TYPE_INT_ARGB);
        ByteBuffer buffer = ByteBuffer.wrap(element.get());
        buffer.rewind();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++)
                image.setRGB(x, y, buffer.getInt());
        }

        return image;
    }

    public static MetaTag serializeFolder(File file) throws IOException {
        if (!file.exists())
            return null;

        if (file.isFile()) {
            long fileSize = file.length();

            if (fileSize > Integer.MAX_VALUE - 5) {
                ListTag<ContainerElement> list = new ListTag<>(file.getName());
                InputStream inputStream = new FileInputStream(file);

                long size = fileSize;
                byte[] dataBuffer = new byte[Integer.MAX_VALUE - 5];
                byte[] BUFFER = new byte[4096];

                while (size > 0) {
                    int toRead = (int) (size > Integer.MAX_VALUE - 5 ? Integer.MAX_VALUE - 5 : size);
                    int written = 0;

                    for (int i = inputStream.read(BUFFER, 0, toRead > BUFFER.length ? BUFFER.length : toRead);
                        i != -1; i = inputStream.read(BUFFER, 0, toRead > BUFFER.length ? BUFFER.length : toRead)) {

                        System.arraycopy(BUFFER, 0, dataBuffer, written, i);
                        written += i;
                        toRead -= i;
                    }

                    list.add(new ContainerTag(file.getName(), dataBuffer));
                    size -= Integer.MAX_VALUE - 5;
                }
                return list;
            } else {
                byte[] data = new byte[(int) fileSize];
                InputStream inputStream = new FileInputStream(file);

                int written = 0;
                byte[] BUFFER = new byte[4096];
                for (int i = inputStream.read(BUFFER); i != -1; i = inputStream.read(BUFFER)) {
                    System.arraycopy(BUFFER, 0, data, written, i);
                    written += i;
                }
                return new ContainerTag(file.getName(), data);
            }
        } else if (file.isDirectory()) {
            DocTag docTag = new DocTag(file.getName());

            File[] files = file.listFiles();
            if (files == null)
                return docTag;

            for (File f : files) {
                if (f == null || !file.exists())
                    continue;

                MetaTag data = serializeFolder(f);
                if (data != null)
                    docTag.insert(data);
            }
            return docTag;
        }
        return null;
    }

    public static QueryResult pullResult(QueryTrace request, ResourceLocation location, MetaData data) throws ResourceUnavailableException {
        if (data != null) {

            if (request.hasNext()) {

                if (data instanceof ResourceLocation)
                    return ((ResourceLocation) data).trace(request.next());
                else
                    throw new ResourceUnavailableException(request);
            }
            return request.pull(location, data);
        }
        return null;
    }

    @Override
    public void start() {
        CerberusRegistry.getInstance().info("Starting cerberus data management service");
    }

    @Override
    public void stop() {
        CerberusRegistry.getInstance().info("Stopping cerberus data management service");
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return CerberusData.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return null;
    }
}
