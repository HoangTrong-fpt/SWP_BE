package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PurchasedTemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    @Schema(description = "Loại template", example = "TEMPLATE_100K",
            allowableValues = {"FREE", "TEMPLATE_100K", "TEMPLATE_200K",
                    "TEMPLATE_300K", "TEMPLATE_500K"})
    private PurchasedTemplateType templateType;
}
