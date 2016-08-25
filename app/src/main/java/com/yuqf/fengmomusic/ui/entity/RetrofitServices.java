package com.yuqf.fengmomusic.ui.entity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    public interface RankingService {
        //q.k?op=query&cont=tree&node=2&pn=1&rn=100&fmt=json&src=mbox&level=3
        @GET("q.k")
        Call<GsonRankingList> getGsonRankingList(@Query("op") String query,
                                                 @Query("cont") String tree,
                                                 @Query("node") String node,
                                                 @Query("pn") String pIndex,
                                                 @Query("rn") String pCount,
                                                 @Query("fmt") String json,
                                                 @Query("src") String mbox,
                                                 @Query("level") String level);
    }

    public interface MusicService {
        //ksong.s?from=pc&fmt=json&type=bang&data=content&id=56&pn=0&rn=200
        @GET("ksong.s")
        Call<GsonRMusicList> getGsonRMusicList(@Query("from") String pc,
                                               @Query("fmt") String json,
                                               @Query("type") String bang,
                                               @Query("data") String content,
                                               @Query("id") String id,
                                               @Query("pn") int pIndex,
                                               @Query("rn") int pCount);

        //r.s?stype=artist2music&artistid=947&pn=0&rn=20
        @GET("r.s")
        Call<ResponseBody> getGsonSMusicList(@Query("stype") String artist2music,
                                             @Query("artistid") String artistId,
                                             @Query("pn") int pIndex,
                                             @Query("rn") int pCount);

        //anti.s?rid=MUSIC_%d&response=url&type=convert_url&format=mp3|aac
        @GET("anti.s")
        Call<ResponseBody> getMusicUrl(@Query("rid") String id,
                                       @Query("response") String url,
                                       @Query("type") String convert_url,
                                       @Query("format") String mp3aac);

        //pic.web?type=rid_pic&pictype=url&size=70&rid=%d
        @GET("pic.web")
        Call<ResponseBody> getCoverUrl(@Query("type") String rid_pic,
                                       @Query("pictype") String url,
                                       @Query("size") int size,
                                       @Query("rid") int id);
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
