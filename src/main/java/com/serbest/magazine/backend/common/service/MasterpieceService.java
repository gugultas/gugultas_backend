package com.serbest.magazine.backend.common.service;

import com.serbest.magazine.backend.common.dto.MasterpieceOfTheWeekResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceRequestDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceResponseDTO;
import com.serbest.magazine.backend.common.dto.MasterpieceUpdateRequestDTO;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;

public interface MasterpieceService {

    MessageResponseDTO create(MasterpieceRequestDTO requestDTO);

    MasterpieceOfTheWeekResponseDTO getMasterpieceOfTheWeek();

    MasterpieceResponseDTO getMasterpieceById(String id);

    MasterpieceResponseDTO updateMasterpiece(String id, MasterpieceUpdateRequestDTO requestDTO);

}
