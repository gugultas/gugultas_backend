package com.serbest.magazine.backend.dto.subcategory;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubCategoryCreateRequestDTO {


    @Length(max = 25,message = "Provide a name less than 25 characters , please.")
    @NotBlank(message = "Provide a valid name , please.")
    private String name;

    @NotBlank(message = "Provide a valid category , please.")
    private String categoryName;

}
