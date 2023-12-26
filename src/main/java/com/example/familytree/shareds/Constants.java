package com.example.familytree.shareds;

import java.util.Date;

public class Constants {
    /* Table name */
    public static final String TABLE_USER = "tbl_userAccount";


    /* Data */
    public static final String GET_DATA_SUCCESS = "Lấy dữ liệu của bảng ''{0}'' thành công ^.^";
    public static final String GET_DATA_EMPTY = "Dữ liệu lấy ra từ bảng ''{0}'' trống!!! ";
    public static final String SEARCH_DATA_SUCCESS = "Tìm kiếm dữ liệu từ bảng ''{0}'' thành công ^.^";
    public static final String SEARCH_DATA_EMPTY = "Không tìm thấy dữ liệu phù hợp trong bảng ''{0}''!!! ";

    // Tree
    public static final String NOT_FOUND_FAMILY_TREE = "FamilyTree có familyTreeId = ''{0}'' không tồn tại!";



    // User

    public static final String USER_DOES_NOT_EXITS_IN_TREE_ID = "Trong cây familyTreeId = ''{0}'' không tồn tại UserId = ''{1}''!!";
    public static final String PERSON_DOES_NOT_EXITS_IN_TREE_ID = "Trong cây FamilyTreeId = ''{0}'' không tồn tại PersonID = ''{1}''!!";

    public static final String USER_DOES_NOT_EXITS_IN_TREE ="User có Id = ''{0}'' không trong cây của Person có Id =''{1}''. Không thể thao tác!";
    // Person

    public static final String GET_INFO_PERSON_SUCCESS = "Lấy thông tin thành công!";

    public static final String UPDATE_PERSON_SUCCESS = "Cập nhật thành công thông tin Person!";

    public static final String NOT_FOUND_PERSON = "Person có personId = ''{0}'' không tồn tại!";
    public static final String NOT_FOUND_FATHER = "Không tồn tại PersonId có giá trị FatherId = ''{0}'' là Nam trong bảng PersonEntity!";
    public static final String NOT_FOUND_MOTHER = "Không tồn tại PersonId có giá trị MotherId = ''{0}'' là Nữ trong bảng PersonEntity!";
    public static final String ADD_PARENTS_SUCCESS = "Thêm thành công Bố hoặc Mẹ!";
    public static final String ADD_CHILD_SUCCESS = "Thêm thành công Con!";

    public static final String NO_PARENTS = "Person có personId = {0} chưa có bố mẹ. Hãy thêm bố hoặc mẹ để sửa!";


    public static final String NOT_ADD_FATHER = "Đã có bố rồi không thể thêm bố nữa!";
    public static final String NOT_ADD_MOTHER = "Đã có mẹ rồi không thể thêm mẹ nữa!";

    public static final String NOT_EDIT_MOTHER = "Person muốn sửa có giá trị personId = ''{0}'' đã có MotherID = ''{1}'' rồi không thể thay đổi!";
    public static final String NOT_EDIT_FATHER = "Person muốn sửa có giá trị personId = ''{0}'' đã có FatherID = ''{1}'' rồi không thể thay đổi!";

    public static final String SIBLING_NOT_NULL = "SiblingNum không được để trống!";
    public static final String RANGE_VALUE_SIBLINGID= "SiblingNum phải là giá trị ''{0}'' + 0.5 hoặc ''{0}'' - 0.5";
    public static final String SIBLING_NOT_A_CHILD = "Sibling ID không phải con của FatherID và MotherID!";

    public static final String FATHERID_OR_MOTHERID_NOT_NULL = "Phải có một trong 2 trường FatherId hoặc MotherId!";

    public static final String NOT_HUSBAND_AND_WIFE = "Person có Id = ''{0}'' và Id = ''{1}'' không phải là vợ chồng!";


    // Spouse
    public static final String NOT_FOUND_SPOUSE = "Không tồn tại SpouseEntity có giá trị spouseId = ''{0}'' !";
    public static final String NOT_FOUND_PARENT_ID = "Không tồn tại SpouseEntity có giá trị ParentId = ''{0}'' trong bảng Spouse!";




    public static final String DELETE_SUCCESS = "Xoá thành công rồi nhé!";
    public static final String UPDATE_SUCCESS = "Cập nhật thành công rồi nhé!";
    public static final String RESTORE_SUCCESS = "Khôi phục thành công rồi nhé!";

    public static final String REQUIRE_TYPE = "`{1}` phải là dạng `{0}`";

    public static final String DUPLICATE_FIELD = "Trường `{0}` của bảng `{1}` đã tồn tại rồi!";

    /* Add Data */
    public static final String SAVE_DATA_SUCCESS = "Dữ liệu đã được lưu thành công.";
    public static final String DUPLICATE_ERROR_ID = "Id có giá trị ''{0}'' đã được sử dụng. Vui lòng sử dụng Id khác";
    /* Register */
    public static final String REGISTER_SUCCESS = "Đăng kí thành công.";
    public static final String CONFIRM_EMAIL = "Vui lòng hãy vào email vừa đăng kí để xác nhận!";

    public static final String DUPLICATE_ERROR_EMAIL = "Email có địa chỉ ''{0}'' đã được đăng ký rồi. Vui lòng dùng Email khác để đăng ký! ";
    public static final String DUPLICATE_ERROR_USERNAME = "Username có giá trị ''{0}'' đã được đăng ký rồi. Vui lòng dùng Username khác để đăng ký! ";

    /* OTP */

    public static final String OTP_SUCCESS = "Otp chính xác ^.^ Bạn đã đổi mật khẩu thành công. ";
    public static final String OTP_FAILED = "Otp không đúng hãy thử lại :((  ";
    public static final String OTP_TIME_OUT = "Otp đã hết hạn :(( Bạn hãy yêu cầu gửi lại otp nhé.";
    public static final String OTP_COUNT_FAIL_ATTEMPT = "Bạn đã nhập sai quá 5 lần. Hãy yêu cầu gửi lại Otp sau 5p nhé!";
    public static final String OTP_NOT_FOUND_USER = "Không tìm thấy người dùng yêu cầu quên mật khẩu!";


    /* Mail */
    public static final long OTP_VALID_DURATION_1P = 60 * 1000;
    public static final long OTP_VALID_DURATION_5P = 5 * 60 * 1000;

    public static final long LINK_SHARING_DURATION = 10 * 24 * 60 * 60 * 1000; // 10 day

    public static final long VERIFICATION_CODE_DURATION = 5 * 60 * 1000;

    public static final String URL_VERIFICATION_CUSTOMER = "http://localhost:8080/users/register/verify?code=";
    public static final String URL_LINK_SHARING = "http://localhost:8080/linkSharing?code=";

    /* JWT */

    public static final long ACCESS_TOKEN_EXP = 24 * 60 * 60 * 1000; // 1 ngày
    public static final long REFRESH_TOKEN_EXP = 30L * 24 * 60 * 60 * 1000; // 1 tháng

    // Notification
    public static final String CREATE_PERSON_MESSAGE = "{0} đã thêm dữ liệu của {1} vào sơ đồ {2}.";
    public static final String UPDATE_PERSON_MESSAGE = "{0} đã sửa dữ liệu của {1} trong sơ đồ {2}.";
    public static final String DELETE_PERSON_MESSAGE = "{0} đã xoá dữ liệu của {1} khỏi sơ đồ {2}.";
    public static final String JOIN_FAMILY_TREE_MESSAGE = "{0} đã tham gia vào sơ đồ {1}";
    public static final String REQUEST_JOIN_FAMILY_TREE_MESSAGE = "{0} yêu cầu tham gia vào sơ đồ {1}";

    public static final String CREATE_PERSON_TYPE = "CREATE_PERSON";
    public static final String UPDATE_PERSON_TYPE = "UPDATE_PERSON";
    public static final String DELETE_PERSON_TYPE = "DELETE_PERSON";
    public static final String JOIN_FAMILY_TREE_TYPE = "JOIN_FAMILY_TREE";
    public static final String REQUEST_JOIN_FAMILY_TREE_TYPE = "REQUEST_JOIN_FAMILY_TREE";

    public static final String userId = "userId";
    public static final String userName = "userName";
    public static final String personId = "personId";
    public static final String personName = "personName";
    public static final String familyTreeId = "familyTreeId";
    public static final String message = "message";




    public final static class SEND_MAIL_SUBJECT {
        public final static String USER_REGISTER = "ĐƯỜNG DẪN XÁC NHẬN THÔNG TIN NGƯỜI DÙNG ĐĂNG KÝ";
        public final static String USER_FORGET_PASSWORD = "MÃ XÁC NHẬN LẤY LẠI TÀI KHOẢN NGƯỜI DÙNG";
        public static final String REQUEST_JOIN_FAMILY_TREE = "Yêu cầu tham gia sơ đồ {0}";
    }
    public final static class TEMPLATE_FILE_NAME {
        public final static String USER_FORGET_PASSWORD = "user_forget_password_email";
        public final static String VERIFY_USER = "verify_user_email";
        public static final String REQUEST_JOIN_FAMILY_TREE = "request_join_family";
    }

    public final static class HISTORY_ENUM {
        public final static String CREATED = "CREATED";
        public final static String UPDATED = "UPDATED";
        public static final String DELETED = "DELETED";
    }

    /* Validate */
    public static final String INVALID_DATA_FIELD = "Dữ liệu không hợp lệ!";
    public static final String INVALID_EMAIL = "Email không hợp lệ!";
    public static final String INVALID_NOTNULL = "Must not be null!";
    public static final String INVALID_EMPTY = "Must not be empty!";
    public static final String INVALID_FILE_IMAGE = "Ảnh phải ở định dạng png hoặc jpg";
    public static final String INVALID_PASSWORD_MIN_LENGTH = "Độ dài mật khẩu phải từ 8 đến 20 kí tự";
    public static final String INVALID_USERNAME_MIN_LENGTH = "Độ dài username phải trong khoảng 6 đến 20 kí tự";
    public static final String INVALID_BIRTHDAY = "Ngày sinh không hợp lệ!";

    /* Message */
//    public static final String


    /* Function */
    public static Date getCurrentDay(){
        java.util.Date currentDate = new java.util.Date();
        return new Date(currentDate.getTime());
    }

    /* Regex */
    public static final String REGEX_URL_IMAGE = "(https?:\\/\\/.*\\.(?:png|jpg))";

}
