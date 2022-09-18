import React from "react";
import { getErrorMessage } from "../helpers/errors";
import { handleError, handleSuccess } from "../helpers/state";
import { getBlocksReq } from "../service/BlockchainService";
import { IBlock } from "../types/block";
import { IRequestState } from "../types/general";

export const useBlocks = () => {
  const [blocks, setBlocks] = React.useState<IRequestState<IBlock[]>>({
    data: [],
    loading: true,
    error: null,
  });

  const initBlocks = async () => {
    try {
      const blocks = await getBlocksReq();
      handleSuccess(blocks, setBlocks);
    } catch (error) {
      handleError(getErrorMessage(error), setBlocks);
      // console.error(error);
    }
  };

  React.useEffect(() => {
    initBlocks();
  }, []);

  return {
    blocks,
  };
};
