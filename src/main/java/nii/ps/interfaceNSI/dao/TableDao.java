package nii.ps.interfaceNSI.dao;

import lombok.extern.slf4j.Slf4j;
import nii.ps.interfaceNSI.model.DictionaryAttrModel;
import nii.ps.interfaceNSI.model.DictionaryModel;
import nii.ps.interfaceNSI.model.TableModel;
import nii.ps.interfaceNSI.utils.MapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TableDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate; // для параметрич-х запросов

    public TableModel getTableModelForSql(String sql) {
        return getTableModelForSql(sql, Collections.emptyMap());
    }

    public TableModel getTableModelForSql(String sql, String key1, Object val1) {
        return getTableModelForSql(sql, MapHelper.of(key1, val1));
    }

    public TableModel getTableModelOrEmpty(String sql, String key1, Object val1) {
        return val1 != null
                ? getTableModelForSql(sql, MapHelper.of(key1, val1))
                : new TableModel(parseColumnsFromSqlOrEmpty(sql), Collections.emptyList());
    }

    public TableModel getTableModelForSql(String sql, Map<String, Object> params) {
        try {
            List<Map<String, Object>> resultRows = namedParameterJdbcTemplate.queryForList(sql, params);

            final List<String> columns = sql.contains("*")
                    ? CollectionUtils.isEmpty(resultRows) ? Collections.emptyList() : parseColumnsFromRow(resultRows.get(0))
                    : parseColumnsFromSql(sql);

            List<List> rows = resultRows.stream()
                    .map(map -> columns.stream().map(map::get).collect(Collectors.toList()))
                    .collect(Collectors.toList());

            return new TableModel(columns, rows);
        } catch (Exception e) {
            log.error("FAILED getTableModelForSql(sql={}, params={})", sql, params);
            return new TableModel(parseColumnsFromSqlOrEmpty(sql), Collections.emptyList());
        }
    }

    private List<String> parseColumnsFromSqlOrEmpty(String sql) {
        return sql.contains("*") ? Collections.emptyList() : parseColumnsFromSql(sql);
    }

    private List<String> parseColumnsFromSql(String sql) {
        String argStr = sql.substring(sql.indexOf("select ") + 7, sql.indexOf(" from "));
        String[] argArray = argStr.split(",");
        List<String> argList = Arrays.asList(argArray);
        return argList.stream().map(String::trim).collect(Collectors.toList());
    }

    private List<String> parseColumnsFromRow(Map<String, Object> row) {
        return new ArrayList<>(row.keySet());
    }

    // Создание категорий
    public Integer createCategoryAndGetIdMain(DictionaryModel dictionary) {
        final Integer idSub = namedParameterJdbcTemplate.queryForObject(
                "select cInsert('subCategoryName', NULL, NULL, 10, NULL, false, NULL, false, 1, true);",
                Collections.emptyMap(),
                Integer.class
        );

        assert idSub != null;
        final Integer idMain = namedParameterJdbcTemplate.queryForObject(
                "select cInsert('mainCategoryName', NULL, NULL, 1, :idSub, false, NULL, false, 1, true);",
                Map.of( "idSub", idSub),
                Integer.class
        );

        dictionary.getAttrs().forEach(attr -> attrInsert(idSub, attr));

        completeCategory(idSub);
        completeCategory(idMain);

        Integer guideId = createTableGuide(idMain, dictionary.getName(), dictionary.getDescription());

        return guideId;
    }

    private void completeCategory(Integer id) {
        namedParameterJdbcTemplate.queryForObject(
                "select cSetCompleted(:id, 1);",
                Map.of("id", id),
                Integer.class
        );
    }

    private void attrInsert(Integer idSub, DictionaryAttrModel attr) {
        namedParameterJdbcTemplate.queryForObject(
                "select acInsert(:idSub, :attrTypeId, :code, :name, 'attribute', " +
                        "NULL, NULL, NULL, NULL, true, false, NULL, NULL, 1, NULL);",
                Map.of(
                        "idSub", idSub,
                        "attrTypeId", attr.getAttrType().getId(),
                        "code", attr.getCode(),
                        "name", attr.getName()
                ),
                Integer.class
        );
    }

    // создание справочника
    public Integer createTableGuide(Integer idMain, String name, String description) {
        Integer guideId = namedParameterJdbcTemplate.queryForObject(
                "select ioInsert(:guideName, :idMain, getCurrentUser(), 1, NULL, :description, NULL, 1,NULL, 1, NULL, false, NULL, NULL, NULL, NULL, 2, NULL, NULL);",
                Map.of("guideName", name, "idMain", idMain, "description", description),
                Integer.class
        );
        completeTableGuide(guideId);
        return guideId;
    }

    private void completeTableGuide(Integer id) {
        namedParameterJdbcTemplate.queryForObject(
                "select ioSetCompleted(:id, 1);",
                Map.of("id", id),
                Integer.class
        );
    }

    public void insertOrUpdate(String sqlInsert, String sqlUpdate, String primaryKey, List<Map<String, Object>> paramList) {
        for (Map<String, Object> params : paramList) {
            if (params.get(primaryKey) != null && !"".equals(params.get(primaryKey))) {
                namedParameterJdbcTemplate.update(sqlUpdate, params);
            } else {
                namedParameterJdbcTemplate.update(sqlInsert, params);
            }
        }
    }

    public void deleteUser(String username) {
        namedParameterJdbcTemplate.update("delete from login_users where username = :username;", Map.of("username", username));
    }

    public void updateAuthority(String username, String authority) {
        namedParameterJdbcTemplate.update(
                "UPDATE login_users SET authority = :authority WHERE username= :username;",
                Map.of("username", username, "authority", authority)
        );
    }

    public void updatePasswordAndAuthority(String username, String password, String authority) {
        namedParameterJdbcTemplate.update(
                "UPDATE login_users SET password = :password, authority = :authority WHERE username= :username;",
                Map.of("username", username, "password", password, "authority", authority)
        );
    }

    public void insertUser(String username, String password, String authority) {
        namedParameterJdbcTemplate.update(
                "insert into login_users (username, password, authority) values(:username, :password, :authority);",
                Map.of("username", username, "password", password, "authority", authority)
        );
    }

    public String getAuthorityByUsername(String username) {
        return namedParameterJdbcTemplate.queryForObject(
                "select authority from login_users where username = :username;",
                Map.of("username", username),
                String.class
        );
    }

    public void guideTableDeleteById(Integer id) {
        namedParameterJdbcTemplate.queryForObject("select ioDelete(:id, false);", Map.of("id", id), Integer.class);
    }

    public void addRow(Integer guideId, String pName) {
        namedParameterJdbcTemplate.update(
                String.format("insert into eio_table_%d (p_type, p_name) values (1, :pName);", guideId),
                Map.of("pName", pName)
        );
    }

    public void updateRow(Integer guideId, Integer id, String pName) {
        namedParameterJdbcTemplate.update(
                String.format("update eio_table_%d set p_name = :pName where id = :id;", guideId),
                Map.of("pName", pName, "id", id)
        );
    }

    public void deleteRow(Integer guideId, Long id) {
        namedParameterJdbcTemplate.queryForObject(
                String.format("select f_del_eio_table_%d (:id);", guideId),
                new MapSqlParameterSource("id", id),
                Integer.class
        );
    }

    public List<Map<String, Object>> getGuideList() {
        return namedParameterJdbcTemplate.query(
                "select id, name from io_objects where id > 300;",
                Collections.emptyMap(),
                (rs, i) -> Map.of("id", rs.getInt("id"), "name", rs.getString("name"))
        );
    }
}