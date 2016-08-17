package com.yuqf.fengmomusic.ui.entity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 2016/8/12.
 */
public class RetrofitServices {
    public interface SingerService {
        ///mb.slist?stype=artistlist&category=1&order=hot&pn=0&rn=100【热门歌手】
        @GET("mb.slist")
        Call<ResponseBody> getGsonSingerList(@Query("stype") String type,
                                               @Query("category") String category,//singer kind
                                               @Query("order") String order,
                                               @Query("pn") String pIndex,    // the server can't provide all kinds, but only one page, this is page Index
                                               @Query("rn") String pCount);   // music count per page, default 100
        @GET("mb.slist")
        Call<ResponseBody> getGsonSingerList(@Query("stype") String type,
                                             @Query("category") String category,//singer kind
                                             @Query("order") String order,
                                             @Query("pn") String pIndex,    // the server can't provide all kinds, but only one page, this is page Index
                                             @Query("rn") String pCount,   // music count per page, default 100
                                             @Query("prefix") String prefix);
    }

//    public static class SingerConverterFactory extends Converter.Factory {
//
//        private static SingerConverterFactory factory;
//
//        public  static SingerConverterFactory create()
//        {
//            if(factory==null)
//                factory = new SingerConverterFactory();
//            return factory;
//        }
//
//        private SingerConverterFactory() {
//            super();
//        }
//
//        @Override
//        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//            return super.responseBodyConverter(type, annotations, retrofit);
//        }
//
//        @Override
//        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//            return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
//        }
//
//        @Override
//        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//            if (type == String.class)
//                return SingerResponseBodyConverter.getInstance();
//            return super.stringConverter(type, annotations, retrofit);
//        }
//    }
//
//    public static class SingerResponseBodyConverter implements Converter<ResponseBody, String> {
//
//        private static SingerResponseBodyConverter instance;
//
//        public static SingerResponseBodyConverter getInstance() {
//            if (instance == null)
//                instance = new SingerResponseBodyConverter();
//            return instance;
//        }
//
//        @Override
//        public String convert(ResponseBody value) throws IOException {
//            return value.toString().replace("'", "\"");
//        }
//    }
}
