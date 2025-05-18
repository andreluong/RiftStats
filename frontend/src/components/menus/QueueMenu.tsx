import { Menu, Button, Tooltip } from "@mantine/core";

// Not implemented yet

export default function QueueMenu() {
  return (
    <Menu shadow="md" width={60}>
      <Menu.Target>
        <Tooltip label="Change Queue Type" withArrow>
          <Button
            variant="outline"
            color="white"
            styles={{
              root: {
                pointerEvents: "none",
              },
            }}
          >
            SOLO/DUO
          </Button>
        </Tooltip>
      </Menu.Target>
    </Menu>
  );
}
