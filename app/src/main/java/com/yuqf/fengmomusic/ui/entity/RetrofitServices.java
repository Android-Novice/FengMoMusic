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

        //        http://search.kuwo.cn/r.s?stype=artistinfo&artistid=
        @GET("r.s?stype=artistinfo")
        Call<ResponseBody> getSingerInfo(@Query("artistid") String artistid);
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

    public interface LyricService {
        //        http://ttlyrics.duapp.com/api/lrc/?sh?Artist=shakira&Title=try%20everything&api=true
//        http://ttlyrics.duapp.com/api/lrc/?dl?Id=-304546313
//        http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=歌曲名&duration=歌曲总时长(毫秒)&hash=歌曲Hash值
//        http://lyrics.kugou.com/download?ver=1&client=pc&id=10515303&accesskey=3A20F6A1933DE370EBA0187297F5477D&fmt=lrc&charset=utf8
        @GET("search?ver=1&man=yes&client=pc&hash=")
        Call<GsonLyricList> getLyricList(@Query("keyword") String music,
                                         @Query("duration") int duration);

        @GET("download?ver=1&client=pc&fmt=lrc&charset=utf8")
        Call<GSonLyric> getLyric(@Query("id") int id,
                                 @Query("accesskey") String accessKey);
    }

    public interface SearchService {
        //t.s?type=2014&c=mbox&w=
        @GET("t.s?type=2014&c=mbox")
        Call<ResponseBody> getRecommendString(@Query(value = "w", encoded = true) String pinyin);

        //        http://search.kuwo.cn/r.s?client=kt&all=ai%25%25*****%25%C4%E3&pn=0&rn=1000
//        &ft=music&plat=pc&cluster=0&result=json&strategy=2012&itemset=newkm&ver=mbox&show_copyright_off=1&vipver=MUSIC_8.4.0.0_W2
        @GET("r.s?client=kt&&ft=music&plat=pc&cluster=0&result=json&strategy=2012&itemset=newkm&ver=mbox&show_copyright_off=1&vipver=MUSIC_8.4.0.0_W2")
        Call<ResponseBody> getSearchString(@Query(value = "all", encoded = true) String searchStr,
                                           @Query("pn") int pageIndex,
                                           @Query("rn") int number);

        //        http://www.kuwo.cn/pc/index/playListMore
        @GET("pc/index/playListMore")
        Call<GSonHotRecommend> getHotRecommend();

        //        pl.svc?op=getlistinfo&pid=1929043096&pn=0&rn=50&encode=utf-8&keyset=pl2012
        @GET("pl.svc?op=getlistinfo&pn=0&rn=100&encode=utf-8&keyset=pl2012")
        Call<GSonHotMusicList> getHotMusicList(@Query("pid") String sourceId);
    }

    public interface RecommendService {
        @GET("pc/index/info")
        Call<ResponseBody> getAllRecommendInfo();

        //        r.s?pn=0&rn=1000&stype=albuminfo&albumid=1375274&show_copyright_off=1&alflac=1&vipver=MUSIC_8.5.0.0_BCS53
        @GET("r.s?pn=0&rn=1000&stype=albuminfo&show_copyright_off=1&alflac=1&vipver=MUSIC_8.5.0.0_BCS53")
        Call<ResponseBody> getRecommendAlbum(@Query("albumid") String albumId);

        @GET("rec.s?cmd=rcm_keyword_playlist&uid=350015241&devid=32289075&platform=pc")
        Call<ResponseBody> getPersonalRecommendation();
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
