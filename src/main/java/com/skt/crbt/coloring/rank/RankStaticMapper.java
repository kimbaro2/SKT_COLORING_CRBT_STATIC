package com.skt.crbt.coloring.rank;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * admin_new/sql templates.
 * sdate/edate are bound. table, cpFilter, orderBy stay as SQL fragments
 * because PHP createSql() replaced those '?' by string substitution.
 */
@Mapper
public interface RankStaticMapper {

    List<Map<String, Object>> selectData(@Param("table") String table,
                                          @Param("sdate") String sdate,
                                          @Param("edate") String edate,
                                          @Param("cpFilter") String cpFilter,
                                          @Param("orderBy") String orderBy);

    List<Map<String, Object>> selectDataT(@Param("table") String table,
                                           @Param("sdate") String sdate,
                                           @Param("edate") String edate,
                                           @Param("cpFilter") String cpFilter,
                                           @Param("orderBy") String orderBy);

    List<Map<String, Object>> selectRank(@Param("table") String table,
                                          @Param("sdate") String sdate,
                                          @Param("edate") String edate,
                                          @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectRankT(@Param("table") String table,
                                           @Param("sdate") String sdate,
                                           @Param("edate") String edate,
                                           @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectTotal(@Param("table") String table,
                                           @Param("sdate") String sdate,
                                           @Param("edate") String edate,
                                           @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectTotalT(@Param("table") String table,
                                            @Param("sdate") String sdate,
                                            @Param("edate") String edate,
                                            @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectViewChannel(@Param("table") String table,
                                                 @Param("sdate") String sdate,
                                                 @Param("edate") String edate,
                                                 @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectViewChannelSum(@Param("table") String table,
                                                    @Param("sdate") String sdate,
                                                    @Param("edate") String edate,
                                                    @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectViewService(@Param("table") String table,
                                                 @Param("sdate") String sdate,
                                                 @Param("edate") String edate,
                                                 @Param("cpFilter") String cpFilter);

    List<Map<String, Object>> selectViewServiceSum(@Param("table") String table,
                                                    @Param("sdate") String sdate,
                                                    @Param("edate") String edate,
                                                    @Param("cpFilter") String cpFilter);
}
