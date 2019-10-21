package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeaModelTest {

    public static class SubModel extends TeaModel {
        public String accessToken;

        @NameInMap("access_key_id")
        public String accessKeyId;

        public String[] list;

        public long size;
    }

    @Test
    public void toModel() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        SubModel submodel = TeaModel.toModel(new HashMap<String, Object>(), new SubModel());
        Assert.assertEquals(null, submodel.accessKeyId);
        SubModel submodel2 = TeaModel.toModel(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("accessToken", "the access token");
                put("access_key_id", "the access key id");
                put("list", new String[]{"string0", "string1"});
            }
        }, new SubModel());

        Assert.assertEquals("the access key id", submodel2.accessKeyId);
        Assert.assertEquals("the access token", submodel2.accessToken);
        Assert.assertArrayEquals(new String[]{"string0", "string1"}, submodel2.list);
    }

    @Test
    public void toMap() throws IllegalArgumentException, IllegalAccessException {
        SubModel submodel = new SubModel();
        submodel.accessToken = "the access token";
        submodel.accessKeyId = "the access key id";
        submodel.list = new String[]{"string0", "string1"};

        Map<String, Object> map = submodel.toMap();
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("the access key id", map.get("access_key_id"));
        Assert.assertEquals("the access token", map.get("accessToken"));
        Assert.assertTrue(map.get("list") instanceof String[]);
        String[] list = (String[]) map.get("list");
        Assert.assertArrayEquals(new String[]{"string0", "string1"}, list);
    }

    public static class BaseDriveResponse extends TeaModel {
        @NameInMap("creator")
        public String creator;

        @NameInMap("description")
        public String description;

        @NameInMap("domain_id")
        public String domainId;

        @NameInMap("drive_id")
        public String driveId;

        @NameInMap("drive_name")
        public String driveName;

        @NameInMap("drive_type")
        public String driveType;

        @NameInMap("owner")
        public String owner;

        @NameInMap("relative_path")
        public String relativePath;

        @NameInMap("status")
        public String status;

        @NameInMap("store_id")
        public String storeId;

        @NameInMap("total_size")
        public Integer totalSize;

        @NameInMap("used_size")
        public Integer usedSize;
    }

    public static class ListDriveResponse extends TeaModel {
        @NameInMap("items")
        public BaseDriveResponse[] items;

        @NameInMap("next_marker")
        public String nextMarker;
    }

    @Test
    public void toMapWithList() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("domainId", "test");
        responseMap.put("status", "test");
        items.add(responseMap);
        map.put("items", items);
        map.put("next_marker", "");
        ListDriveResponse response = TeaModel.toModel(map, new ListDriveResponse());
        Assert.assertTrue(response.items[0] instanceof BaseDriveResponse);
    }

    public static class HelloResponse extends TeaModel {
        @NameInMap("data")
        public Hello data;

    }

    public static class Hello extends TeaModel {
        @NameInMap("message")
        public String message;
    }

    @Test
    public void toMapWithGeneric() throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("message", "Hello jacksontian");
            }
        });

        HelloResponse response = TeaModel.toModel(map, new HelloResponse());
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.data);
        Assert.assertEquals("Hello jacksontian", response.data.message);
    }

    @Test
    public void parseToIntTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class teaModel = TeaModel.class;
        Method parseToInt = teaModel.getDeclaredMethod("parseToInt", Object.class);
        parseToInt.setAccessible(true);
        Object arg = null;
        Object result = parseToInt.invoke(teaModel, arg);
        Assert.assertNull(result);

        arg = 2D;
        result = parseToInt.invoke(teaModel, arg);
        Assert.assertEquals(2, result);

        arg = 2.32D;
        result = parseToInt.invoke(teaModel, arg);
        Assert.assertEquals(2.32D, result);

        arg = Integer.MAX_VALUE + 1D;
        result = parseToInt.invoke(teaModel, arg);
        Assert.assertEquals(Integer.MAX_VALUE + 1L, result);

        arg = 2L;
        result = parseToInt.invoke(teaModel, arg);
        Assert.assertEquals(2, result);

        arg = Integer.MAX_VALUE + 1L;
        result = parseToInt.invoke(teaModel, arg);
        Assert.assertEquals(Integer.MAX_VALUE + 1L, result);
    }

    @Test
    public void transformFieldTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("size", Double.valueOf("6"));
        SubModel submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals(6L, submodel.size);

        map.put("size", Double.valueOf(Integer.MAX_VALUE + 1L));
        submodel = TeaModel.toModel(map, new SubModel());
        Assert.assertEquals(Integer.MAX_VALUE + 1L, submodel.size);
    }
}
