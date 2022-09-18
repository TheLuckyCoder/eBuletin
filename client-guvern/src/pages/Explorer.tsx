import { Button, Link, Tooltip, Typography } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { useState } from "react";
import { Transactions } from "../components/Blocks/Transactions";
import { useBlocks } from "../hooks/useBlocks";
import { ITransaction } from "../types/transaction";

export const Explorer = () => {
  const { blocks } = useBlocks();
  const [transactions, setTransactions] = useState<ITransaction[] | null>(null);

  const columns: GridColDef[] = [
    { field: "blockNumber", headerName: "Block Number", flex: 1 },
    {
      field: "parentHash",
      headerName: "Parent Hash",
      flex: 1,
      renderCell: (params) => (
        <Tooltip title={params.value as string}>
          <Typography>{params.value}</Typography>
        </Tooltip>
      ),
    },
    {
      field: "timestamp",
      headerName: "Timestamp",
      flex: 1,
      renderCell: (params) => {
        const date = new Date(params.value * 1000);
        return date.toLocaleString();
      },
    },
    {
      field: "transactions",
      headerName: "Transactions",
      flex: 1,
      renderCell: (params) => (
        <Button size="small" onClick={() => setTransactions(params.value)}>
          See Transactions
        </Button>
      ),
    },
  ];

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
      <Transactions transactions={transactions} />
    </>
  );
};
