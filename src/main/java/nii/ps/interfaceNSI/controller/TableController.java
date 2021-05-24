package nii.ps.interfaceNSI.controller;

import lombok.extern.slf4j.Slf4j;
import nii.ps.interfaceNSI.dao.TableDao;
import nii.ps.interfaceNSI.model.DictionaryAttrModel;
import nii.ps.interfaceNSI.model.DictionaryModel;
import nii.ps.interfaceNSI.model.TableModel;
import nii.ps.interfaceNSI.model.enums.DataType;
import nii.ps.interfaceNSI.service.CsvService;
import nii.ps.interfaceNSI.service.ExcelService;
import nii.ps.interfaceNSI.service.SqlUtilService;
import nii.ps.interfaceNSI.service.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class TableController {
    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String ATTACHMENT_FILENAME = "attachment;filename=";

    @Autowired
    private TableDao tableDao;
    @Autowired
    private CsvService csvService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SqlUtilService sqlUtilService;

    // USER METHODS ----------------------------------------------------

    // simple requests
    @GetMapping("/user")
    public String getMainUser() {
        return "user/main_user";
    }

    @GetMapping("/guide_table_user")
    public String getGuideTableUser() {
        return "user/guide_table_user";
    }

    @GetMapping("/guide_table_list_user")
    public String getGuideTableListUser(Model model) {
        TableModel tableModel = tableDao.getTableModelForSql(
                "select id, last_update, name, description from io_objects where id > 300;");
        tableModel.addButtonsPreview(row -> "/guide_table_selected_fields_user?number=" + row.get(0));
        model.addAttribute("tableModel", tableModel);
        return "user/guide_table_list_user";
    }

    @GetMapping("/guide_table_selected_user")
    public String getGuideTableSelectedUser(Model model, @RequestParam(required = false) Integer number) {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        model.addAttribute("tableModel", tableModel);
        model.addAttribute("guideList", tableDao.getGuideList());
        return "user/guide_table_selected_user";
    }

    @GetMapping("/guide_table_selected_fields_user")
    public String getGuideTableSelectedFieldsUser(Model model, @RequestParam(required = false) Integer number) {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null);", number)
        );
        model.addAttribute("tableModel", tableModel);
        model.addAttribute("guideList", tableDao.getGuideList());
        return "user/guide_table_selected_fields_user";
    }

    @GetMapping("/attribute_type_list_user")
    public String getAttributeTypeListUser(Model model) {
        model.addAttribute("tableModel", tableDao
                .getTableModelForSql("select id, last_update, name, code from a_types;"));
        return "user/attribute_type_list_user";
    }

    // download to Excel
    @GetMapping("/guide_table_list_user_xls")
    public void getGuideTableListUserXls(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, description from io_objects where id > 300;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_user_xls")
    public void getGuideTableSelectedUserXls(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_fields_user_xls")
    public void getGuideTableSelectedFieldsUserXls(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null););", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/attribute_type_list_user_xls")
    public void getAttributeTypeListUserXLS(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, code from a_types;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    // download to .csv
    @GetMapping("/guide_table_list_user_csv")
    public void getGuideTableListUserCsv(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, description from io_objects where id > 300;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_user_csv")
    public void getGuideTableSelectedUserCsv(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_fields_user_csv")
    public void getGuideTableSelectedFieldsUserCsv(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }
    @GetMapping("/attribute_type_list_user_csv")
    public void getAttributeTypeListUserCsv(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, code from a_types;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }



    // ADMIN METHODS  --------------------------------------------------

    // simple requests
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getMainAdmin() {
        return "admin/main_admin";
    }

    @GetMapping("/guide_table_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableAdmin() {
        return "admin/guide_table_admin";
    }

    @GetMapping("/guide_table_list_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableListAdmin(Model model) {
        TableModel tableModel = tableDao.getTableModelForSql(
                "select id, last_update, name, description from io_objects where id > 300;");
        tableModel.addButtonsPreview(row -> "/guide_table_selected_fields_admin?number=" + row.get(0));
        tableModel.addButtonsDelete(row -> "/guide_table_list_admin_delete?id=" + row.get(0));
        model.addAttribute("tableModel", tableModel);
        return "admin/guide_table_list_admin";
    }

    @GetMapping("/guide_table_list_admin_delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableDeleteAdmin(@RequestParam Integer id) {
        tableDao.guideTableDeleteById(id);
        return "redirect:/guide_table_list_admin";
    }

    @GetMapping("/guide_table_selected_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableSelectedAdmin(Model model, @RequestParam(required = false) Integer number) {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        model.addAttribute("tableModel", tableModel);
        model.addAttribute("guideList", tableDao.getGuideList());
        return "admin/guide_table_selected_admin";
    }

    @GetMapping("/guide_table_selected_fields_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableSelectedFieldsAdmin(Model model, @RequestParam(required = false) Integer number) {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null);", number)
        );
        tableModel.addButtonsUpdate(row -> "/guide_table_row_update_admin/" + number + "/" + row.get(0));
        tableModel.addButtonsDelete(row -> "/guide_table_selected_fields_admin_delete?guideId=" + number + "&id=" + row.get(0));
        model.addAttribute("tableModel", tableModel);
        model.addAttribute("guideList", tableDao.getGuideList());
        return "admin/guide_table_selected_fields_admin";
    }

    // add Row URL
    @GetMapping("/guide_table_row_add_admin/{guideId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableRowAdminAdd(Model model, @PathVariable Integer guideId) {
        model.addAttribute("guideId", guideId);
        return "admin/guide_table_row_add_admin";
    }
    // add Row URL
    @PostMapping("/guide_table_row_add_admin/{guideId}")
    public String postGuideTableRowAdminAdd(Model model, @PathVariable Integer guideId, @RequestParam String pName) {
        tableDao.addRow(guideId, pName);
        return "redirect:/guide_table_selected_fields_admin?number=" + guideId;
    }

    // update Row URL
    @GetMapping("/guide_table_row_update_admin/{guideId}/{id}") // .../@PathVariable Integer guideId/@PathVariable Integer id
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableRowAdminUpdate(Model model, @PathVariable Integer guideId, @PathVariable Integer id) {
        model.addAttribute("guideId", guideId);
        model.addAttribute("id", id);
        return "admin/guide_table_row_update_admin";
    }
    // update Row URL
    @PostMapping("/guide_table_row_update_admin/{guideId}/{id}")
    public String postGuideTableRowAdminUpdate(
            @PathVariable Integer guideId,
            @PathVariable Integer id,
            @RequestParam String pName
    ) {
        tableDao.updateRow(guideId, id, pName);
        return "redirect:/guide_table_selected_fields_admin?number=" + guideId;
    }

    // delete Row URL
    @GetMapping("/guide_table_selected_fields_admin_delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableRowDeleteAdmin(Model model, @RequestParam Integer guideId, @RequestParam Long id) {
        tableDao.deleteRow(guideId, id);
        return "redirect:/guide_table_selected_fields_admin?number=" + guideId;
    }

    @GetMapping("/attribute_type_list_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAttributeTypeListAdmin(Model model) {
        model.addAttribute("tableModel", tableDao
                .getTableModelForSql("select id, last_update, name, code from a_types;"));
        return "admin/attribute_type_list_admin";
    }

    @GetMapping("/guide_table_create_complete_admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getGuideTableCreateCompleteAdmin(Model model) {
        model.addAttribute("dictionaryModel", DictionaryModel.builder().attrs(List.of(new DictionaryAttrModel())).build());
        return "admin/guide_table_create_complete_admin";
    }

    @PostMapping("/guide_table_create_complete_admin")
    public String postGuideTableCreateCompleteAdmin(@ModelAttribute DictionaryModel dictionaryModel) {
        log.info(dictionaryModel.toString());
        Integer guideId = tableDao.createCategoryAndGetIdMain(dictionaryModel);
        return "redirect:/guide_table_selected_admin?number=" + guideId;
    }

    // download to Excel
    @GetMapping("/guide_table_list_admin_xls")
    public void getGuideTableListAdminXls(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, description from io_objects where id > 300;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_admin_xls")
    public void getGuideTableSelectedAdminXls(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_fields_admin_xls")
    public void getGuideTableSelectedFieldsAdminXls(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/attribute_type_list_admin_xls")
    public void getAttributeTypeListAdminXLS(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, code from a_types;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.xls");
        response.setContentType(EXCEL_CONTENT_TYPE);
        excelService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    // download to .csv
    @GetMapping("/guide_table_list_admin_csv")
    public void getGuideTableListAdminCsv(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, description from io_objects where id > 300;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_admin_csv")
    public void getGuideTableSelectedAdminCsv(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id_attribute, id_attr_type, attr_code, attr_name, attr_type_name from cGetCategoryAttrsByIO(%d, false);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/guide_table_selected_fields_admin_csv")
    public void getGuideTableSelectedFieldsAdminCsv(HttpServletResponse response, @RequestParam(required = false) Integer number) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql(
                String.format("select id, uuid_t, p_name, p_type, p_type_fk from f_sel_eio_table_%d_ex(null);", number)
        );
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    @GetMapping("/attribute_type_list_admin_csv")
    public void getAttributeTypeListAdminCsv(HttpServletResponse response) throws IOException {
        TableModel tableModel = tableDao.getTableModelForSql("select id, last_update, name, code from a_types;");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "file.csv");
        response.setContentType(CSV_CONTENT_TYPE);
        csvService.generateAndWriteToStream(tableModel, response.getOutputStream());
    }

    // upload from Excel
    @PostMapping("/guide_table_selected_fields_admin_upload_xls")
    public String getGuideTableSelectedFieldsAdminUploadXls(
            @RequestParam Integer number,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes attributes
    ) throws IOException {
        log.info(file.getOriginalFilename());
        List<List> rows = uploadService.parse(file.getInputStream(), file.getOriginalFilename());
        List<Map<String, Object>> paramList = sqlUtilService.createParamList(rows, List.of("id", "p_name"));
        if (file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            sqlUtilService.changeTypes(paramList, Map.of("id", DataType.INTEGER, "p_name", DataType.STRING));
        }
        tableDao.insertOrUpdate(
                String.format("insert into eio_table_%d (p_name, p_type) values (:p_name, 1);", number),
                String.format("update eio_table_%d set p_name = :p_name where id = :id;", number),
                "id",
                paramList
        );
        return "redirect:/guide_table_selected_fields_admin?number=" + number;
    }

    // upload from .csv
    @PostMapping("/guide_table_selected_fields_admin_upload_csv")
    public void getGuideTableSelectedFieldsAdminUploadCsv(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes attributes
    ) {
        log.info(file.getName());
    }
}
