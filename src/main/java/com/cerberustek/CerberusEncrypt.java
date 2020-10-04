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

import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.elements.*;
import com.cerberustek.data.impl.tags.MapTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.service.CerberusService;
import com.cerberustek.settings.Settings;
import com.cerberustek.settings.impl.SettingsImpl;
import com.cerberustek.utils.DiscriminatorFile;
import org.jetbrains.annotations.NotNull;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class CerberusEncrypt implements CerberusService {

    private static final String SETTINGS_PATH = "security/settings.xml";

    private Settings settings;
    private DocElement document;
    private SecureRandom random;

    @Override
    public void start() {
        CerberusRegistry registry = CerberusRegistry.getInstance();
        settings = new SettingsImpl(new File(SETTINGS_PATH));
        settings.init();

        registry.debug("Reading key-set...");
        DiscriminatorMap map = createOrReadMap();

        try (MetaInputStream inputStream = CerberusData
                .createInputStream(new FileInputStream(
                        settings.getString("certificates", "ssh-certs.cdf")), map)) {

            MetaData data = inputStream.readData();
            if (data instanceof DocElement) {
                document = (DocElement) data;

                registry.info("Read encryption certificates.");
            } else {
                registry.warning("Failed to read encryption certificates;");
                registry.warning("Creating empty document instead.");
                document = new DocElement();
            }
        } catch (IOException | UnknownDiscriminatorException e) {
            registry.warning("Failed to read encryption certificates!");
            registry.warning("Creating empty document instead.");
            document = new DocElement();
        }

        random = new SecureRandom();
    }

    private DiscriminatorMap createOrReadMap() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        DiscriminatorFile discFile = new DiscriminatorFile(new File(
                settings.getString("discriminators", "Encoding.cdf")));
        DiscriminatorMap map;
        if (!discFile.getFile().exists()) {
            map = CerberusData.genDefaultDiscriminators();
            try {
                discFile.write(map);
            } catch (IOException | UnknownDiscriminatorException e) {
                registry.warning("Failed to write encrypting discriminators!");
            }
        } else {
            try {
                map = discFile.read();
            } catch (FileNotFoundException e) {
                registry.warning("Failed to read encrypting discriminators.");
                registry.warning("Using default discriminators instead.");
                map = CerberusData.genDefaultDiscriminators();
            }
        }
        return map;
    }

    @NotNull
    private MapTag<UUIDElement, SecretKeyElement> getSecretKeySet() {
        //noinspection unchecked
        MapTag<UUIDElement, SecretKeyElement> map = (MapTag<UUIDElement, SecretKeyElement>) document.extractMap("secret_keys");
        if (map == null) {
            map = new MapTag<>("secret_keys");
            document.insert(map);
        }
        return map;
    }

    @NotNull
    private MapTag<UUIDElement, PrivateKeyElement> getPrivateKeySet() {
        //noinspection unchecked
        MapTag<UUIDElement, PrivateKeyElement> map = (MapTag<UUIDElement, PrivateKeyElement>) document.extractMap("private_keys");
        if (map == null) {
            map = new MapTag<>("private_keys");
            document.insert(map);
        }
        return map;
    }

    @NotNull
    private MapTag<UUIDElement, PublicKeyElement> getPublicKeySet() {
        //noinspection unchecked
        MapTag<UUIDElement, PublicKeyElement> map = (MapTag<UUIDElement, PublicKeyElement>) document.extractMap("public_keys");
        if (map == null) {
            map = new MapTag<>("public_keys");
            document.insert(map);
        }
        return map;
    }

    @NotNull
    private MapTag<UUIDElement, ContainerElement> getSaltSet() {
        @SuppressWarnings("unchecked") MapTag<UUIDElement, ContainerElement> map
                = (MapTag<UUIDElement, ContainerElement>) document.extractMap("salts");
        if (map == null) {
            map = new MapTag<>("salts");
            document.insert(map);
        }
        return map;
    }

    @Override
    public void stop() {
        save();
        settings.destroy();
    }

    public void save() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        DiscriminatorMap map = createOrReadMap();
        try (MetaOutputStream outputStream = CerberusData.createOutputStream(
                new FileOutputStream(settings.getString("certificates", "ssh-certs.cdf")), map)) {

            outputStream.writeData(document);
            outputStream.flush();
        } catch (IOException | NoMatchingDiscriminatorException e) {
            registry.warning("Failed to write ssh-certificates.");
        }
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return CerberusEncrypt.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return null;
    }

    public void registerPublicKey(UUID uuid, PublicKey key) {
        if (getPublicKeySet().containsKey(new UUIDElement(uuid)))
            getPublicKeySet().replace(new UUIDElement(uuid), new PublicKeyElement(key));
        else
            getPublicKeySet().put(new UUIDElement(uuid), new PublicKeyElement(key));
    }

    public UUID registerPublicKey(PublicKey key) {
        UUID uuid = nextUID();
        getPublicKeySet().put(new UUIDElement(uuid), new PublicKeyElement(key));
        return uuid;
    }

    public void registerPrivateKey(UUID uuid, PrivateKey key) {
        if (getPrivateKeySet().containsKey(new UUIDElement(uuid)))
            getPrivateKeySet().replace(new UUIDElement(uuid), new PrivateKeyElement(key));
        else
            getPrivateKeySet().put(new UUIDElement(uuid), new PrivateKeyElement(key));
    }

    public UUID registerPrivateKey(PrivateKey key) {
        UUID uuid = nextUID();
        getPrivateKeySet().put(new UUIDElement(uuid), new PrivateKeyElement(key));
        return uuid;
    }

    public void registerSecretKey(SecretKey key, UUID uuid) {
        if (getSecretKeySet().containsKey(new UUIDElement(uuid)))
            getSecretKeySet().replace(new UUIDElement(uuid), new SecretKeyElement(key));
        else
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
    }

    public UUID registerSecretKey(SecretKey key) {
        UUID uuid = nextUID();
        getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
        return uuid;
    }

    public PublicKey getPublicKey(UUID uuid) {
        PublicKeyElement key = getPublicKeySet().get(new UUIDElement(uuid));
        return key != null ? key.get() : null;
    }

    public PrivateKey getPrivateKey(UUID uuid) {
        PrivateKeyElement key = getPrivateKeySet().get(new UUIDElement(uuid));
        return key != null ? key.get() : null;
    }

    public SecretKey getSecretKey(UUID uuid) {
        SecretKeyElement key = getSecretKeySet().get(new UUIDElement(uuid));
        return key != null ? key.get() : null;
    }

    public SecretKey genSecretKey(UUID uuid) {
        return genSecretKey(uuid, settings.getInteger("key_length", 256));
    }

    public SecretKey genSecretKey(UUID uuid, int keyLength) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(keyLength, random);
            SecretKey key = generator.generateKey();
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return key;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your System does not support AES encryption!");
        }
        return null;
    }

    public SecretKey genSecretKey(UUID uuid, String algorithm) {
        return genSecretKey(uuid, algorithm, settings.getInteger("key_length", 256));
    }

    public SecretKey genSecretKey(UUID uuid, String algorithm, int keyLength) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(algorithm);
            generator.init(keyLength, random);
            SecretKey key = generator.generateKey();
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return key;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your System does not support " + algorithm + " encryption!");
        }
        return null;
    }

    public UUID genSecretKey(String algorithm, char[] pw, byte[] salt, int iterationCount, int keyLength) {
        try {
            UUID uuid = nextUID();
            registerSalt(uuid, salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, iterationCount, keyLength);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return uuid;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    public SecretKey genSecretKey(UUID uuid, String algorithm, char[] pw, byte[] salt, int iterationCount, int keyLength) {
        try {
            getSaltSet().remove(new UUIDElement(uuid));
            getSecretKeySet().remove(new UUIDElement(uuid));
            registerSalt(uuid, salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, iterationCount, keyLength);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return key;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    public UUID genSecretKey(char[] pw, byte[] salt) {
        return genSecretKey("AES", pw, salt);
    }

    public UUID genSecretKey(String algorithm, char[] pw, byte[] salt) {
        try {
            // remove is already present
            UUID uuid = nextUID();
            registerSalt(uuid, salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, settings.getInteger("key_spec_iterations", 65536),
                    settings.getInteger("key_length", 256));
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return uuid;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    public SecretKey genSecretKey(UUID uuid, String algorithm, char[] pw, byte[] salt) {
        try {
            // remove is already present
            getSecretKeySet().remove(new UUIDElement(uuid));
            getSaltSet().remove(new UUIDElement(uuid));
            registerSalt(uuid, salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, settings.getInteger("key_spec_iterations", 65536),
                    settings.getInteger("key_length", 256));
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return key;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    public UUID genSecretKey(char[] pw) {
        return genSecretKey("AES", pw);
    }

    public UUID genSecretKey(String algorithm, char[] pw) {
        try {
            UUID uuid = nextUID();
            byte[] salt = genSalt(uuid);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, settings.getInteger("key_spec_iterations", 65536),
                    settings.getInteger("key_length", 256));
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return uuid;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    public SecretKey genSecretKey(UUID uuid, char[] pw) {
        return genSecretKey(uuid, "AES", pw);
    }

    public SecretKey genSecretKey(UUID uuid, String algorithm, char[] pw) {
        try {
            // remove is already present
            getSecretKeySet().remove(new UUIDElement(uuid));
            getSaltSet().remove(new UUIDElement(uuid));
            byte[] salt = genSalt(uuid);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pw, salt, settings.getInteger("key_spec_iterations", 65536),
                    settings.getInteger("key_length", 256));
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), algorithm);
            getSecretKeySet().put(new UUIDElement(uuid), new SecretKeyElement(key));
            return key;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your system does not support " + algorithm + " encryption!");
        } catch (InvalidKeySpecException e) {
            CerberusRegistry.getInstance().critical("Cannot generate secret key from password ************");
        }
        return null;
    }

    private byte[] genSalt(UUID uuid, int size) {
        byte[] salt = randomIV(size);
        getSaltSet().put(new UUIDElement(uuid), new ContainerElement(salt));
        return salt;
    }

    public void registerSalt(UUID uuid, byte[] salt) {
        getSaltSet().put(new UUIDElement(uuid), new ContainerElement(salt));
    }

    public void removeSalt(UUID uuid) {
        getSaltSet().remove(new UUIDElement(uuid));
    }

    private byte[] genSalt(UUID uuid) {
        return genSalt(uuid, settings.getInteger("salt_size", 8));
    }

    public byte[] getSalt(UUID uuid) {
        ContainerElement raw = getSaltSet().get(new UUIDElement(uuid));
        if (raw == null)
            return null;
        return raw.get();
    }

    public Mac getMac(UUID uuid) {
        return null;
    }

    public Mac genMac(UUID uuid) {
        return null;
    }

    public void removeMac(UUID uuid) {

    }

    public RSAPublicKey getRSAPublicKey(UUID uuid) {
        PublicKey key = getPublicKey(uuid);
        if (key instanceof RSAPublicKey)
            return (RSAPublicKey) key;
        return null;
    }

    public RSAPrivateKey getRSAPrivateKey(UUID uuid) {
        PrivateKey key = getPrivateKey(uuid);
        if (key instanceof RSAPrivateKey)
            return (RSAPrivateKey) key;
        return null;
    }

    public KeyPair genRSAKeyPair(UUID uuid, int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
            KeyPair pair = generator.genKeyPair();

            getPrivateKeySet().put(new UUIDElement(uuid), new PrivateKeyElement(pair.getPrivate()));
            getPublicKeySet().put(new UUIDElement(uuid), new PublicKeyElement(pair.getPublic()));

            return pair;
        } catch (NoSuchAlgorithmException e) {
            CerberusRegistry.getInstance().critical("Your System does not support RSA encryption!");
        }
        return null;
    }

    public KeyPair genRSAKeyPair(UUID uuid) {
        return genRSAKeyPair(uuid, settings.getInteger("rsa_keysize", 3072));
    }

    private UUID nextUID() {
        UUID uuid = UUID.randomUUID();
        // make sure the key uuid is unique
        while (getPublicKeySet().containsKey(new UUIDElement(uuid))
                || getPrivateKeySet().containsKey(new UUIDElement(uuid))
                || getSecretKeySet().containsKey(new UUIDElement(uuid)))
            uuid = UUID.randomUUID();
        return uuid;
    }

    public UUID genRSAKeyPair(int keySize) {
        UUID uuid = nextUID();
        genRSAKeyPair(uuid, keySize);
        return uuid;
    }

    public UUID genRSAKeyPair() {
        UUID uuid = nextUID();
        genRSAKeyPair(uuid);
        return uuid;
    }

    public DSAPublicKey getDSAPublicKey(UUID uuid) {
        PublicKey key = getPublicKey(uuid);
        if (key instanceof DSAPublicKey)
            return (DSAPublicKey) key;
        return null;
    }

    public DSAPrivateKey getDSAPrivateKey(UUID uuid) {
        PrivateKey key = getPrivateKey(uuid);
        if (key instanceof DSAPrivateKey)
            return (DSAPrivateKey) key;
        return null;
    }

    public KeyPair genDSAKeyPair(UUID uuid, int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
            generator.initialize(keySize);
            KeyPair pair = generator.genKeyPair();

            getPrivateKeySet().put(new UUIDElement(uuid), new PrivateKeyElement(pair.getPrivate()));
            getPublicKeySet().put(new UUIDElement(uuid), new PublicKeyElement(pair.getPublic()));

            return pair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair genDSAKeyPair(UUID uuid) {
        return genDSAKeyPair(uuid, settings.getInteger("dsa_keysize", 3072));
    }

    public UUID genDSAKeyPair(int keySize) {
        UUID uuid = nextUID();
        genDSAKeyPair(uuid, keySize);
        return uuid;
    }

    public UUID genDSAKeyPair() {
        UUID uuid = nextUID();
        genDSAKeyPair(uuid);
        return uuid;
    }

    public boolean hasPrivateKey(UUID uuid) {
        return getPrivateKeySet().containsKey(new UUIDElement(uuid));
    }

    public boolean hasPublicKey(UUID uuid) {
        return getPublicKeySet().containsKey(new UUIDElement(uuid));
    }

    public boolean hasSecretKey(UUID uuid) {
        return getSecretKeySet().containsKey(new UUIDElement(uuid));
    }

    public String getKeyType(UUID uuid) {
        PrivateKeyElement privateKey = getPrivateKeySet().get(new UUIDElement(uuid));
        if (privateKey != null)
            return privateKey.getTypeString();

        PublicKeyElement publicKey = getPublicKeySet().get(new UUIDElement(uuid));
        if (publicKey != null)
            return publicKey.getTypeString();
        return null;
    }

    public void removeKey(UUID uuid) {
        getPrivateKeySet().remove(new UUIDElement(uuid));
        getPublicKeySet().remove(new UUIDElement(uuid));
        getSecretKeySet().remove(new UUIDElement(uuid));
    }

    public void removePrivateKey(UUID uuid) {
        getPrivateKeySet().remove(new UUIDElement(uuid));
    }

    public void removePublicKey(UUID uuid) {
        getPublicKeySet().remove(new UUIDElement(uuid));
    }

    public void removeSecretKey(UUID uuid) {
        getSecretKeySet().remove(new UUIDElement(uuid));
    }

    public byte[] randomIV(int length) {
        byte[] out = new byte[length];
        random.nextBytes(out);
        return out;
    }

    public byte[] randomIV(byte[] iv) {
        random.nextBytes(iv);
        return iv;
    }

    public SecureRandom getRandom() {
        return random;
    }
}
