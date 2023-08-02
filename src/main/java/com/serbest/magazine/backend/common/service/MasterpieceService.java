package com.serbest.magazine.backend.common.service;

import com.serbest.magazine.backend.common.dto.*;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;

import java.util.List;

public interface MasterpieceService {

    MessageResponseDTO create(MasterpieceRequestDTO requestDTO);

    MasterpieceOfTheWeekResponseDTO getMasterpieceOfTheWeek();

    List<MasterpieceListResponseDTO> getMasterpieces();

    MasterpieceResponseDTO getMasterpieceById(String id);

    MasterpieceResponseDTO updateMasterpiece(String id, MasterpieceUpdateRequestDTO requestDTO);

}
