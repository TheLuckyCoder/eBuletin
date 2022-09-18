import { Link } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { useBlocks } from "../hooks/useBlocks";

const columns: GridColDef[] = [
  { field: "blockNumber", headerName: "Block Number", flex: 1 },
  { field: "hash", headerName: "Hash", flex: 1 },
  { field: "timestamp", headerName: "Timestamp", flex: 1 },
  {
    field: "transactions",
    headerName: "Transactions",
    flex: 1,
    renderCell: () => <Link>transactions</Link>,
  },
];

export const Explorer = () => {
  const { blocks } = useBlocks();
  
  return (
    <>
      <DataGrid
        columns={columns}
        rows={blocks.data}
        loading={blocks.loading}
        getRowId={(row) => row.blockNumber}
        autoHeight
        error={blocks.error ? true : undefined}
        pageSize={10}
        rowsPerPageOptions={[10]}
      />
    </>
  );
};
