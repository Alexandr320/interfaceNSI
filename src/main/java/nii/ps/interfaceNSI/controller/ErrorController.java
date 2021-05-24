package nii.ps.interfaceNSI.controller;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";

    @Autowired
    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(value = ERROR_PATH, method = RequestMethod.GET)
    public ResponseEntity<?> handleErrors(HttpServletRequest request) throws NotFoundException {
        HttpStatus status = getStatus(request);

        if (status.equals(HttpStatus.NOT_FOUND))
            throw new NotFoundException("Not found");

        return ResponseEntity.status(status).body(getErrorAttributes(request, false));
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
